import {Component, OnInit} from '@angular/core';
import {TransactionService} from '../transaction-service/transaction.service';
import {AlertsService} from '../../alert/alerts-service/alerts.service';
import {AccountPriceEntry, Transaction} from '../transaction';
import {Account} from '../../account/account';
import {Category} from '../../category/category';
import {CategoryService} from '../../category/category-service/category.service';
import {AccountService} from '../../account/account-service/account.service';
import {TransactionFilter} from '../transaction-filter';
import {TransactionFilterService} from '../transaction-filter-service/transaction-filter.service';
import {FiltersComponentBase} from './transactions-filter.component';
import {TranslateService} from '@ngx-translate/core';
import {DatePipe} from '@angular/common';

@Component({
  selector: 'app-transactions',
  templateUrl: './transactions.component.html',
  styleUrls: ['./transactions.component.css']
})
export class TransactionsComponent extends FiltersComponentBase implements OnInit {
  allTransactions: Transaction[] = [];
  transactions: Transaction[] = [];
  plannedTransactions: Transaction[] = [];
  categories: Category[] = [];
  accounts: Account[] = [];
  addingMode = false;
  newTransaction = new Transaction();
  selectedFilter = new TransactionFilter();
  originalFilter = new TransactionFilter();
  filters: TransactionFilter[] = [];
  hidePlannedTransactionsCheckboxState = false;
  pipe = new DatePipe('en-US');

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

    // TODO do in parallel with forkJoin (not working for some reason)
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
            if (transaction.isPlanned) {
              this.plannedTransactions.push(transaction);
            } else {
              this.transactions.push(transaction);
            }
            this.allTransactions.push(transaction);
          }

          super.filterTransactions();
        });
  }

  deleteTransaction(transactionToDelete) {
    const deleteMessageKey = transactionToDelete.isPlanned ? 'message.plannedTransactionDeleted' : 'message.transactionDeleted';
    const deleteDialogMessageKey = transactionToDelete.isPlanned ? 'message.wantDeletePlannedTransaction' : 'message.wantDeleteTransaction';
    if (confirm(this.translate.instant(deleteDialogMessageKey))) {
      this.transactionService.deleteTransaction(transactionToDelete.id)
          .subscribe(() => {
            this.alertService.success(this.translate.instant(deleteMessageKey));
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
          this.transactionService.getTransaction(transaction.id)
              .subscribe(updatedTransaction => {
                const messageKey = updatedTransaction.planned ? 'message.plannedTransactionEdited' : 'message.transactionEdited';
                this.alertService.success(this.translate.instant(messageKey));
                const returnedTransaction = new Transaction(); // TODO dupliated code
                returnedTransaction.date = updatedTransaction.date;
                returnedTransaction.id = updatedTransaction.id;
                returnedTransaction.description = updatedTransaction.description;
                returnedTransaction.isPlanned = updatedTransaction.planned;

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

  addTransaction() {
    if (!this.validateTransaction(this.newTransaction)) {
      return;
    }

    this.transactionService.addTransaction(this.newTransaction)
        .subscribe(id => {
          this.transactionService.getTransaction(id)
              .subscribe(createdTransaction => {
                const messageKey = createdTransaction.planned ? 'message.plannedTransactionAdded' : 'message.transactionAdded';
                this.alertService.success(this.translate.instant(messageKey));

                // TODO duplicate with above method
                const returnedTransaction = new Transaction();
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
                this.newTransaction = new Transaction();
                // 2 entries is usually enough, if user needs more he can edit created transaction and then new entry will appear automatically.
                this.newTransaction.accountPriceEntries.push(new AccountPriceEntry());
                this.newTransaction.accountPriceEntries.push(new AccountPriceEntry());
                this.newTransaction.date = new Date;
              });
        });
  }

  commitPlannedTransaction(transaction: Transaction) {
    const deleteDialogMessageKey = 'message.wantCommitPlannedTransactionBeforeDate';
    if (this.isTransactionDateCurrentDate(transaction)) {
      this.commit(transaction);
      return;
    }
    if (confirm(this.translate.instant(deleteDialogMessageKey))) {
      this.commit(transaction);
    }
  }

  private commit(transaction: Transaction) {
    {
      this.transactionService.commitPlannedTransaction(transaction)
          .subscribe(() => {
              this.alertService.success(
                this.translate.instant('message.plannedTransactionCommitted')
              );
            },
            (error) => {
              console.log(error.error[0]);
              // lukasz todo extract to keys
              if (error.error[0] === 'Cannot schedule planned transaction of archived account.') {
                if (confirm(' Archived account detected - the transaction cannot be committed. Choose another account or delete the transaction')) {
                }
              }
            },
            () => this.refreshTransactions()
          );
    }
  }

  private isTransactionDateCurrentDate(transaction) {
    const now = this.pipe.transform(Date.now(), 'longDate');
    const transactionDate = this.pipe.transform(transaction.date, 'longDate');
    return transactionDate === now;
  }

  private refreshTransactions() {
    this.getTransactions();
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

  allFilteredTransactionsBalance() {
    let sum = 0;

    for (let i = 0; i < this.transactions.length; ++i) {
      for (let j = 0; j < this.transactions[i].accountPriceEntries.length; ++j) {
        if (this.transactions[i].isPlanned === false) {
          sum += +this.transactions[i].accountPriceEntries[j].price
            * +this.transactions[i].accountPriceEntries[j].account.currency.exchangeRate;
        }
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
