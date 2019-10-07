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
import {DateHelper} from '../../../helpers/date-helper';
import {Operation} from './transaction';

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
            const transaction = this.getTransactionFromResponse(transactionResponse);
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
    if (!this.validateTransaction(transactionToDelete, Operation.Delete)) {
      return;
    }

    const deleteMessageKey = transactionToDelete.isPlanned ? 'message.plannedTransactionDeleted' : 'message.transactionDeleted';
    const deleteDialogMessageKey = transactionToDelete.isPlanned ? 'message.wantDeletePlannedTransaction' : 'message.wantDeleteTransaction';
    if (confirm(this.translate.instant(deleteDialogMessageKey))) {
      this.transactionService.deleteTransaction(transactionToDelete.id)
          .subscribe(() => {
            this.alertService.success(this.translate.instant(deleteMessageKey));
            this.removeDeletedFromDOM(transactionToDelete);
          });
    }
  }

  private removeDeletedFromDOM(transactionToDelete) {
    this.transactions = this.transactions.filter(transaction => transaction !== transactionToDelete);
    this.allTransactions = this.allTransactions.filter(transaction => transaction !== transactionToDelete);
  }

  updateTransaction(transaction: Transaction) {
    if (!this.validateTransaction(transaction.editedTransaction, Operation.Update)) {
      return;
    }
    this.transactionService.editTransaction(transaction.editedTransaction)
        .subscribe((commitBodyResponse) => {

          const updatedTransaction = this.getTransactionFromResponse(commitBodyResponse.committed);
          const messageKey = updatedTransaction.isPlanned ? 'message.plannedTransactionEdited' : 'message.transactionEdited';
          this.alertService.success(this.translate.instant(messageKey));

          Object.assign(transaction, updatedTransaction);
        });
  }

  addTransaction() {
    if (!this.validateTransaction(this.newTransaction, Operation.Add)) {
      return;
    }

    this.transactionService.addTransaction(this.newTransaction)
        .subscribe(id => {
            this.transactionService.getTransaction(id)
                .subscribe(createdTransaction => {
                  const messageKey = createdTransaction.planned ? 'message.plannedTransactionAdded' : 'message.transactionAdded';
                  this.alertService.success(this.translate.instant(messageKey));

                  // TODO duplicate with above method
                  const returnedTransaction = this.getTransactionFromResponse(createdTransaction);

                  this.transactions.push(returnedTransaction);
                  this.allTransactions.push(returnedTransaction);
                  this.addingMode = false;
                  this.newTransaction = new Transaction();
                  // 2 entries is usually enough, if user needs more he can edit created transaction and then new entry will appear automatically.
                  this.newTransaction.accountPriceEntries.push(new AccountPriceEntry());
                  this.newTransaction.accountPriceEntries.push(new AccountPriceEntry());
                  this.newTransaction.date = new Date;
                });
          },
          () => {
            alert(this.translate.instant('message.futureDate'));
          }
        );
  }

  commitPlannedTransaction(transaction: Transaction) {
    if (!this.validateTransaction(transaction, Operation.Commit)) {
      return;
    }
    const deleteDialogMessageKey = 'message.wantCommitPlannedTransactionBeforeDate';
    if (this.isTransactionDateCurrentDate(transaction)) {
      this.commit(transaction);
      return;
    }
    if (confirm(this.translate.instant(deleteDialogMessageKey))) {
      this.commit(transaction);
    }
  }

  commitOverduePlannedTransaction(transaction: Transaction) {
    const commitDialogMessageKey = 'message.wantCommitPlannedTransactionBeforeDate';

    if (confirm(this.translate.instant(commitDialogMessageKey))) {
      this.commit(transaction, 'message.OverduePlannedTransactionCommitted');
    }
  }

  private commit(transaction: Transaction, messageKey?: string) {
    this.transactionService.commitPlannedTransaction(transaction)
        .subscribe((commitBodyResponse) => {
            this.alertService.success(
              this.translate.instant(messageKey == null ? 'message.plannedTransactionCommitted' : messageKey)
            );
            const committedToRemove = this.getTransactionFromResponse(commitBodyResponse.committed);
            Object.assign(transaction, committedToRemove);

            if (commitBodyResponse.scheduledForNextMonth.accountPriceEntries !== undefined) {
              const returnedTransaction = this.getTransactionFromResponse(commitBodyResponse.scheduledForNextMonth);

              this.transactions.push(returnedTransaction);
              this.allTransactions.push(returnedTransaction);
            }
          },
        );
  }

  private getTransactionFromResponse(transactionResponse) {
    const transaction = new Transaction();
    transaction.date = transactionResponse.date;
    transaction.id = transactionResponse.id;
    transaction.description = transactionResponse.description;
    transaction.isPlanned = transactionResponse.planned;
    transaction.isRecurrent = transactionResponse.recurrent;

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
    return transaction;
  }

  setAsRecurrent(transaction: Transaction) {
    this.transactionService.setAsRecurrent(transaction)
        .subscribe(() => {
            this.alertService.success(
              this.translate.instant('message.transactionSetRecurrent'));
            transaction.isRecurrent = true;
          }
        );

  }

  setAsNotRecurrent(transaction: Transaction) {
    this.transactionService.setAsNotRecurrent(transaction)
        .subscribe(() => {
            this.refreshTransactions();

            this.alertService.success(
              this.translate.instant('message.transactionSetNotRecurrent'));
            transaction.isRecurrent = false;

          }
        );

  }

  private isTransactionDateCurrentDate(transaction) {
    const currentDate = this.pipe.transform(Date.now(), 'longDate');
    const transactionDate = this.pipe.transform(transaction.date, 'longDate');
    return transactionDate === currentDate;
  }

  private refreshTransactions() {
    this.getTransactions();
  }

  private validateTransaction(transaction: Transaction, operation: Operation): boolean {
    let status = true;

    if (operation === Operation.Update) {
      if (!transaction.isPlanned && DateHelper.isFutureDate(transaction.date)) {
        this.alertService.error(this.translate.instant('message.transactionFutureDate'));
        status = false;
      }

    }

    if (operation === Operation.Add || operation === Operation.Update) {

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
    }

    if (operation === Operation.Delete) {
      if (!transaction.isPlanned && this.containsArchivedAccount(transaction)) {
        this.alertService.error(this.translate.instant('message.transactionUsingArchivedAccountCannotBeDeleted'));
        status = false;
      }
    }

    if (operation === Operation.Commit) {
      if (transaction.isPlanned && this.containsArchivedAccount(transaction)) {
        this.alertService.error(this.translate.instant('message.plannedTransactionUsingArchivedAccountCannotBeCommitted'));
        status = false;
      }
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

  private containsArchivedAccount(transaction: Transaction): boolean {
    for (const entry of transaction.accountPriceEntries) {
      if (entry.account.archived) {
        return true;
      }
    }
    return false;
  }

  getWarnBgColorForUncommittedPlannedTransaction(transaction: any) {
    if (this.isOverduePlannedTransaction(transaction)) {
      return '#F1AD8D';
    }
  }

  private isOverduePlannedTransaction(transaction: any) {
    return transaction.isPlanned && DateHelper.isPastDate(transaction.date);
  }

  private isNotOverduePlannedTransaction(transaction: any) {
    return transaction.isPlanned && !DateHelper.isPastDate(transaction.date);
  }

}
