import {Component, OnInit} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {TransactionService} from '../components/transaction/transaction-service/transaction.service';
import {AlertsService} from '../components/alert/alerts-service/alerts.service';
import {AccountPriceEntry, Transaction} from '../components/transaction/transaction';
import {CategoryService} from '../components/category/category-service/category.service';
import {TransactionFilter} from '../components/transaction/transaction-filter';
import { FiltersComponentBase } from '../components/transaction/transactions/transactions-filter.component';
import {Category} from '../components/category/category';
import {Account} from '../components/account/account';
import {AccountService} from '../components/account/account-service/account.service';
import {TransactionFilterService} from '../components/transaction/transaction-filter-service/transaction-filter.service';

@Component({
  selector: 'app-planned-transactions',
  templateUrl: '../planned-transaction/planned-transactions.component.html',
  styleUrls: ['../components/transaction/transactions/transactions.component.css']
})
export class PlannedTransactionsComponent extends FiltersComponentBase implements OnInit {
  allTransactions: Transaction[] = [];
  transactions: Transaction[] = [];
  categories: Category[] = [];
  accounts: Account[] = [];
  addingMode = false;
  newTransaction = new Transaction(false);
  selectedPlannedFilter = new TransactionFilter();
  originalFilter = new TransactionFilter();
  filters: TransactionFilter[] = [];

  constructor(
    private transactionService: TransactionService,
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
                this.getTransactions();
                this.getFilters();
              });
        });

    // 2 entries is usually enough, if user needs more he can edit created transaction and then new entry will appear automatically.
    this.newTransaction.accountPriceEntries.push(new AccountPriceEntry());
    this.newTransaction.accountPriceEntries.push(new AccountPriceEntry());
    this.newTransaction.date = new Date();
  }

  getTransactions(): void {
    this.transactionService.getTransactions()
        .subscribe(transactions => {
          this.transactions = [];
          this.allTransactions = [];
          for (const transactionResponse of transactions) {
            const transaction = new Transaction();
            transaction.date = transactionResponse.date;
            transaction.id = transactionResponse.id;
            transaction.description = transactionResponse.description;
            transaction.isPlanned = transactionResponse.planned;
            // fixme here nie przepisuje wartosci logicznej z transaction response
            // transaction.planned = false;

            for (const entry of transactionResponse.accountPriceEntries) {
              const accountPriceEntry = new AccountPriceEntry();
              accountPriceEntry.price = +entry.price; // + added to convert to number

              // need to have same object to allow dropdown to work correctly
              for (const account of this.accounts) { // TODO use hashmap
                if (account.id === entry.accountId) {
                  accountPriceEntry.account = account;
                }
              }

              accountPriceEntry.pricePLN = +entry.price * accountPriceEntry.account.currency.exchangeRate;

              transaction.accountPriceEntries.push(accountPriceEntry);
            }

            for (const category of this.categories) {
              if (category.id === transactionResponse.categoryId) {
                transaction.category = category;
              }
            }

            this.transactions.push(transaction);
            this.allTransactions.push(transaction);
          }

          super.filterPlannedTransactions();
        });
  }

  deleteTransaction(transactionToDelete) {
    if (confirm(this.translate.instant('message.wantDeleteTransaction'))) {
      this.transactionService.deleteTransaction(transactionToDelete.id)
          .subscribe(() => {
            this.alertService.success(this.translate.instant('message.transactionDeleted'));
            this.transactions = this.transactions.filter(transaction => transaction !== transactionToDelete);
            this.allTransactions = this.allTransactions.filter(transaction => transaction !== transactionToDelete);
          });
    }
  }

  updateTransaction(transaction: Transaction) {
    if (!this.validateTransaction(transaction.editedTransaction)) {
      return;
    }

    this.transactionService.editTransaction(transaction.editedTransaction)
        .subscribe(() => {
          this.alertService.success(this.translate.instant('message.transactionEdited'));
          this.transactionService.getTransaction(transaction.id)
              .subscribe(updatedTransaction => {
                const returnedTransaction = new Transaction(false); // TODO dupliated code
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


                Object.assign(transaction, returnedTransaction);
              });
        });
  }

  addPlannedTransaction() {
    if (!this.validateTransaction(this.newTransaction)) {
      return;
    }

    this.transactionService.addPlannedTransaction(this.newTransaction)
        .subscribe(id => {
          this.alertService.success(this.translate.instant('message.transactionAdded'));
          this.transactionService.getTransaction(id)
              .subscribe(createdTransaction => {

                // TODO duplicate with above method
                const returnedTransaction = new Transaction(true);
                returnedTransaction.date = createdTransaction.date;
                returnedTransaction.id = createdTransaction.id;
                returnedTransaction.description = createdTransaction.description;
                returnedTransaction.isPlanned = createdTransaction.planned;


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
                this.allTransactions.push(returnedTransaction);
                this.addingMode = false;
                this.newTransaction = new Transaction(true);
                // 2 entries is usually enough, if user needs more he can edit created transaction and then new entry will appear automatically.
                this.newTransaction.accountPriceEntries.push(new AccountPriceEntry());
                this.newTransaction.accountPriceEntries.push(new AccountPriceEntry());
                this.newTransaction.date = new Date();
              });
        });
  }

  private validateTransaction(transaction: Transaction): boolean {
    let status = true;

    if (transaction.date == null || transaction.date.toString() === '') {
      this.alertService.error(this.translate.instant('message.dateEmpty'));
      status = false;
    }

    if (transaction.description == null || transaction.description.trim() === '') {
      this.alertService.error(this.translate.instant('message.descriptionEmpty'));
      status = false;
    }

    if (transaction.description != null && transaction.description.length > 100) {
      this.alertService.error(this.translate.instant('message.categoryNameTooLong'));
      status = false;
    }

    for (const entry of transaction.accountPriceEntries) {
      if (entry.price == null && entry.account == null && transaction.accountPriceEntries.length > 1) {
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

    if (transaction.category == null) {
      this.alertService.error(this.translate.instant('message.categoryNameEmpty'));
      status = false;
    }

    return status;
  }

  onShowEditMode(transaction: Transaction) {
    transaction.editedTransaction = JSON.parse(JSON.stringify(transaction));
    transaction.editMode = true;

    for (const entry of transaction.editedTransaction.accountPriceEntries) {
      entry.price = +entry.price; // + added to convert to number

      // need to have same object to allow dropdown to work correctly
      for (const account of this.accounts) { // TODO use hashmap
        if (account.id === entry.account.id) {
          entry.account = account;
        }
      }

    }

    // Adds empty entry, thanks to that new value can be added on the UI
    transaction.editedTransaction.accountPriceEntries.push(new AccountPriceEntry());

    for (const category of this.categories) {
      if (category.id === transaction.editedTransaction.category.id) {
        transaction.editedTransaction.category = category;
      }
    }

  }

  allFiltredTransactionsBalance() {
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
