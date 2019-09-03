import {Component, OnInit} from '@angular/core';
import {Category} from '../components/category/category';
import {Account} from '../components/account/account';
import {AlertsService} from '../components/alert/alerts-service/alerts.service';
import {CategoryService} from '../components/category/category-service/category.service';
import {AccountService} from '../components/account/account-service/account.service';
import {TranslateService} from '@ngx-translate/core';
import {PlannedTransactionService} from './planned-transaction-service/planned-transaction.service';
import {TransactionFilterService} from '../components/transaction/transaction-filter-service/transaction-filter.service';
import {FiltersComponentBase} from '../components/transaction/transactions/transactions-filter.component';
import {AccountPriceEntry, Transaction} from '../components/transaction/transaction';

@Component({
  selector: 'app-planned-transactions',
  templateUrl: './planned-transactions.component.html',
  styleUrls: ['./planned-transactions.component.css']
})
export class PlannedTransactionsComponent extends FiltersComponentBase implements OnInit {
  allPlannedTransactions: Transaction[] = [];
  transactions: Transaction[] = [];
  categories: Category[] = [];
  accounts: Account[] = [];
  addingMode = false;
  newTransaction = new Transaction(true);

  constructor(private plannedTransactionService: PlannedTransactionService,
              alertService: AlertsService,
              private categoryService: CategoryService,
              private accountService: AccountService,
              filterService: TransactionFilterService,
              translate: TranslateService) {
    super(alertService, filterService, translate);

  }

  ngOnInit() {
    this.filters = [];
    this.addShowAllFilter();

    this.categoryService.getCategories()
        .subscribe(categories => {
          this.categories = categories;
          this.categories.sort((categories1, categories2) => (categories1.name.toLowerCase() > categories2.name.toLowerCase() ? 1 : -1));

          this.accountService.getAccounts()
              .subscribe(accounts => {
                this.accounts = accounts;
                this.accounts.sort((accounts1, accounts2) => (accounts1.name.toLowerCase() > accounts2.name.toLowerCase() ? 1 : -1));
                this.getPlannedTransactions();
                this.getFilters();
              });
        });

    // 2 entries is usually enough, if user needs more he can edit created transaction and then new entry will appear automatically.
    this.newTransaction.accountPriceEntries.push(new AccountPriceEntry());
    this.newTransaction.accountPriceEntries.push(new AccountPriceEntry());
    this.newTransaction.date = new Date();
  }

  getPlannedTransactions(): void {
    this.plannedTransactionService.getPlannedTransactions()
        .subscribe(plannedTransactions => {
          this.transactions = [];
          this.allPlannedTransactions = [];
          for (const plannedTransactionResponse of plannedTransactions) {
            const plannedTransaction = new Transaction(true);
            plannedTransaction.date = plannedTransactionResponse.date;
            plannedTransaction.id = plannedTransactionResponse.id;
            plannedTransaction.description = plannedTransactionResponse.description;
            plannedTransaction.isPlanned = plannedTransactionResponse.isPlanned;

            for (const entry of plannedTransactionResponse.accountPriceEntries) {
              const accountPriceEntry = new AccountPriceEntry();
              accountPriceEntry.price = +entry.price; // + added to convert to number

              // need to have same object to allow dropdown to work correctly
              for (const account of this.accounts) { // TODO use hashmap
                if (account.id === entry.accountId) {
                  accountPriceEntry.account = account;
                }
              }

              accountPriceEntry.pricePLN = +entry.price * accountPriceEntry.account.currency.exchangeRate;

              plannedTransaction.accountPriceEntries.push(accountPriceEntry);
            }

            for (const category of this.categories) {
              if (category.id === plannedTransactionResponse.categoryId) {
                plannedTransaction.category = category;
              }
            }

            this.transactions.push(plannedTransaction);
            this.allPlannedTransactions.push(plannedTransaction);
          }

          super.filterPlannedTransactions();
        });
  }

  deleteTransaction(plannedTransactionToDelete) {
    if (confirm(this.translate.instant('message.wantDeleteTransaction'))) {
      this.plannedTransactionService.deleteTransaction(plannedTransactionToDelete.id)
          .subscribe(() => {
            this.alertService.success(this.translate.instant('message.plannedTransactionDeleted'));
            this.transactions = this.transactions.filter(plannedTransaction => plannedTransaction !== plannedTransactionToDelete);
            this.allPlannedTransactions = this.allPlannedTransactions.filter(plannedTransaction => plannedTransaction !== plannedTransactionToDelete);
          });
    }
  }

  updateTransaction(plannedTransaction: Transaction) {
    if (!this.validatePlannedTransaction(plannedTransaction.editedTransaction)) {
      return;
    }

    this.plannedTransactionService.editTransaction(plannedTransaction.editedTransaction)
        .subscribe(() => {
          this.alertService.success(this.translate.instant('message.plannedTransactionEdited'));
          this.plannedTransactionService.getPlannedTransaction(plannedTransaction.id)
              .subscribe(updatedTransaction => {
                const returnedTransaction = new Transaction(true); // TODO dupliated code
                returnedTransaction.date = updatedTransaction.date;
                returnedTransaction.id = updatedTransaction.id;
                returnedTransaction.description = updatedTransaction.description;

                for (const entry of updatedTransaction.accountPriceEntries) {
                  const accountPriceEntry = new AccountPriceEntry();
                  accountPriceEntry.price = +entry.price; // + added to convert to number

                  // need to have same object to allow dropdown to work correctly
                  for (const account of this.accounts) { // TODO use hashmap
                    if (account.id === entry.accountId) {
                      accountPriceEntry.account = account;
                    }
                  }

                  accountPriceEntry.pricePLN = +entry.price * accountPriceEntry.account.currency.exchangeRate;

                  returnedTransaction.accountPriceEntries.push(accountPriceEntry);
                }

                for (const category of this.categories) {
                  if (category.id === updatedTransaction.categoryId) {
                    returnedTransaction.category = category;
                  }
                }


                Object.assign(plannedTransaction, returnedTransaction);
              });
        });
  }

  addPlannedTransaction() {
    if (!this.validatePlannedTransaction(this.newTransaction)) {
      return;
    }

    this.plannedTransactionService.addPlannedTransaction(this.newTransaction)
        .subscribe(id => {
          this.alertService.success(this.translate.instant('message.plannedTransactionAdded'));
          this.plannedTransactionService.getPlannedTransaction(id)
              .subscribe(createdTransaction => {

                // TODO duplicate with above method
                const returnedTransaction = new Transaction(true);
                returnedTransaction.date = createdTransaction.date;
                returnedTransaction.id = createdTransaction.id;
                returnedTransaction.description = createdTransaction.description;

                for (const entry of createdTransaction.accountPriceEntries) {
                  const accountPriceEntry = new AccountPriceEntry();
                  accountPriceEntry.price = +entry.price; // + added to convert to number

                  // need to have same object to allow dropdown to work correctly
                  for (const account of this.accounts) { // TODO use hashmap
                    if (account.id === entry.accountId) {
                      accountPriceEntry.account = account;
                    }
                  }

                  accountPriceEntry.pricePLN = +entry.price * accountPriceEntry.account.currency.exchangeRate;

                  returnedTransaction.accountPriceEntries.push(accountPriceEntry);
                }

                for (const category of this.categories) {
                  if (category.id === createdTransaction.categoryId) {
                    returnedTransaction.category = category;
                  }
                }

                this.transactions.push(returnedTransaction);
                this.allPlannedTransactions.push(returnedTransaction);
                this.addingMode = false;
                this.newTransaction = new Transaction(true);
                // 2 entries is usually enough, if user needs more he can edit created transaction and then new entry will appear
                // automatically.
                this.newTransaction.accountPriceEntries.push(new AccountPriceEntry());
                this.newTransaction.accountPriceEntries.push(new AccountPriceEntry());
                this.newTransaction.date = new Date();
              });
        });
  }

  private validatePlannedTransaction(plannedTransaction: Transaction): boolean {
    let status = true;

    if (plannedTransaction.date == null || plannedTransaction.date.toString() === '') {
      this.alertService.error(this.translate.instant('message.dateEmpty'));
      status = false;
    }

    if (plannedTransaction.description == null || plannedTransaction.description.trim() === '') {
      this.alertService.error(this.translate.instant('message.descriptionEmpty'));
      status = false;
    }

    if (plannedTransaction.description != null && plannedTransaction.description.length > 100) {
      this.alertService.error(this.translate.instant('message.categoryNameTooLong'));
      status = false;
    }

    for (const entry of plannedTransaction.accountPriceEntries) {
      if (entry.price == null && entry.account == null && plannedTransaction.accountPriceEntries.length > 1) {
        continue;
      }

      if (entry.price == null) {
        this.alertService.error(this.translate.instant('message.priceEmpty'));
        status = false;
      }

      if (entry.account == null) {
        this.alertService.error(this.translate.instant('message.accountNameEmpty'));
        status = false;
      }
    }

    if (plannedTransaction.category == null) {
      this.alertService.error(this.translate.instant('message.categoryNameEmpty'));
      status = false;
    }

    return status;
  }

  onShowEditMode(plannedTransaction: Transaction) {
    plannedTransaction.editedTransaction = JSON.parse(JSON.stringify(plannedTransaction));
    plannedTransaction.editMode = true;

    for (const entry of plannedTransaction.editedTransaction.accountPriceEntries) {
      entry.price = +entry.price; // + added to convert to number

      // need to have same object to allow dropdown to work correctly
      for (const account of this.accounts) { // TODO use hashmap
        if (account.id === entry.account.id) {
          entry.account = account;
        }
      }

    }

    // Adds empty entry, thanks to that new value can be added on the UI
    plannedTransaction.editedTransaction.accountPriceEntries.push(new AccountPriceEntry());

    for (const category of this.categories) {
      if (category.id === plannedTransaction.editedTransaction.category.id) {
        plannedTransaction.editedTransaction.category = category;
      }
    }

  }

  allFiltredPlannedTransactionsBalance() {
    let sum = 0;

    for (let i = 0; i < this.transactions.length; ++i) {
      for (let j = 0; j < this.transactions[i].accountPriceEntries.length; ++j) {
        sum += +this.transactions[i].accountPriceEntries[j].price
          * +this.transactions[i].accountPriceEntries[j].account.currency.exchangeRate;
      }
    }

    return sum;
  }

  private parseDate(dateString: string): Date {
    if (dateString) {
      return new Date(dateString);
    } else {
      return null;
    }
  }

}
