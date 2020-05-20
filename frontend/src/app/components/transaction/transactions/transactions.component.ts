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
import {Operation} from './operation';
import {RecurrencePeriod} from '../recurrence-period';
import {PostTransactionAccountBalanceHelper} from '../../../helpers/postTransactionAccountBalanceHelper';
import {environment} from '../../../../environments/environment';

interface TransactionsByType {
  past: Transaction[];
  planned: Transaction[];
}

@Component({
  selector: 'app-transactions',
  templateUrl: './transactions.component.html',
  styleUrls: ['./transactions.component.scss']
})
export class TransactionsComponent extends FiltersComponentBase implements OnInit {

  constructor(
    private postTransactionAccountBalanceHelper: PostTransactionAccountBalanceHelper,
    private transactionService: TransactionService,
    alertService: AlertsService,
    private categoryService: CategoryService,
    private accountService: AccountService,
    filterService: TransactionFilterService,
    translate: TranslateService) {
    super(alertService, filterService, translate);
  }

  allTransactions: Transaction[] = [];
  transactions: Transaction[] = [];
  categories: Category[] = [];
  accounts: Account[] = [];
  addingMode = false;
  newTransaction = new Transaction();
  selectedFilter = new TransactionFilter();
  originalFilter = new TransactionFilter();
  filters: TransactionFilter[] = [];
  pipe = new DatePipe('en-US');

  private static setEditionDisabledEntriesToEqualOriginalTransactionValues(transaction: Transaction) {
    transaction.editedTransaction.date = transaction.date;
    transaction.editedTransaction.accountPriceEntries = transaction.accountPriceEntries;
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

  getNotArchivedAccounts(): Account[] {
    const notArchivedAccounts: Account[] = [];
    for (const account of this.accounts) {
      if (!account.archived) {
        notArchivedAccounts.push(account);
      }
    }

    return notArchivedAccounts;
  }

  getTransactions(): void {
    this.transactionService.getTransactions()
        .subscribe(transactions => {
          this.transactions = [];
          this.allTransactions = [];
          for (const transactionResponse of transactions) {
            const transaction = this.getTransactionFromResponse(transactionResponse);
            this.transactions.push(transaction);
            this.allTransactions.push(transaction);
          }
          this.calculateAndAssignPostTransactionBalances();
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
            this.updateCachedAccountBalanceAfterTransactionDeleted(transactionToDelete);
            this.calculateAndAssignPostTransactionBalances();
          });
    }
  }

  private removeDeletedFromDOM(transactionToDelete) {
    this.transactions = this.transactions.filter(transaction => transaction !== transactionToDelete);
    this.allTransactions = this.allTransactions.filter(transaction => transaction !== transactionToDelete);
  }

  updateTransaction(transaction: Transaction) {
    if (!this.validateTransaction(transaction, Operation.Update)) {
      return;
    }
    if (this.isEditingTransactionWithArchivedAccount(transaction)) {
      TransactionsComponent.setEditionDisabledEntriesToEqualOriginalTransactionValues(transaction);
    }
    this.transactionService.editTransaction(transaction.editedTransaction)
        .subscribe((commitResult) => {
          this.transactionService.getTransaction(commitResult.savedTransactionId)
              .subscribe(
                (transactionResponse) => {
                  const saved = this.getTransactionFromResponse(transactionResponse);
                  this.updateCachedAccountBalanceAfterTransactionUpdated(transaction, saved);
                  Object.assign(transaction, saved);
                  this.calculateAndAssignPostTransactionBalances();
                  const messageKey = saved.isPlanned ? 'message.plannedTransactionEdited' : 'message.transactionEdited';
                  this.alertService.success(this.translate.instant(messageKey));
                }
              );
        });
  }

  addTransaction() {
    if (!this.validateTransaction(this.newTransaction, Operation.Add)) {
      return;
    }

    this.transactionService.addTransaction(this.newTransaction)
        .subscribe(id => {
            this.transactionService.getTransaction(id)
                .subscribe(savedTransaction => {
                  const messageKey = savedTransaction.planned ? 'message.plannedTransactionAdded' : 'message.transactionAdded';
                  this.alertService.success(this.translate.instant(messageKey));

                  // TODO duplicate with above method
                  const returnedTransaction = this.getTransactionFromResponse(savedTransaction);

                  this.transactions.push(returnedTransaction);
                  this.allTransactions.push(returnedTransaction);
                  this.updateCachedAccountBalanceAfterTransactionAdded(returnedTransaction);
                  this.calculateAndAssignPostTransactionBalances();
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

  private calculateAndAssignPostTransactionBalances() {
    const transactionsByType = this.getTransactionsByType(this.transactions);
    this.postTransactionAccountBalanceHelper.calculateAndAssignPostTransactionsBalances(transactionsByType.past, false);
    this.postTransactionAccountBalanceHelper.calculateAndAssignPostTransactionsBalances(transactionsByType.planned, true);
  }

  private getTransactionsByType: Function = (transactions: Transaction[]) => {
    const pastTransactions = [];
    const plannedTransactions = [];
    const transactionsByType: TransactionsByType = {
      past: pastTransactions,
      planned: plannedTransactions
    };
    transactions.forEach(transaction => {
      if (transaction.isPlanned) {
        transactionsByType.planned.push(transaction);
      } else {
        transactionsByType.past.push(transaction);
      }
    });
    return transactionsByType;
  }

  private updateCachedAccountBalanceAfterTransactionAdded(addedTransaction: Transaction) {
    this.updateCachedAccountBalanceAfterCrudOperation(this.add, addedTransaction);
  }

  private updateCachedAccountBalanceAfterTransactionUpdated(newTransaction: Transaction, oldTransaction: Transaction) {
    this.updateCachedAccountBalanceAfterCrudOperation(this.add, oldTransaction);
    this.updateCachedAccountBalanceAfterCrudOperation(this.subtract, newTransaction);
  }

  private updateCachedAccountBalanceAfterTransactionDeleted(transactionToDelete: Transaction) {
    this.updateCachedAccountBalanceAfterCrudOperation(this.subtract, transactionToDelete);
  }

  private updateCachedAccountBalanceAfterCrudOperation(updateFunction: Function, newTransaction: Transaction) {
    if (!newTransaction.isPlanned) {
      const accounts = [];
      const prices = [];
      for (let i = 0; i < newTransaction.accountPriceEntries.length; i++) {
        accounts[i] = newTransaction.accountPriceEntries[i].account;
        prices[i] = newTransaction.accountPriceEntries[i].price;

        const accountToUpdate = this.accounts.find((value) => accounts[i].id === value.id
        );
        accountToUpdate.balance = updateFunction(+accountToUpdate.balance, prices[i]);
      }
    }
  }

  private add: Function = (x: number, y: number) => {
    return x + y;
  }

  private subtract: Function = (x: number, y: number) => {
    return x - y;
  }

  private getCurrentDate() {
    const date = new Date();
    const year = date.getFullYear().toString();
    let month = (date.getMonth() + 1).toString();
    let day = date.getDate().toString();
    if (day.length === 1) {
        day = '0' + day;
    }
    if (month.length === 1) {
      month = '0' + month;
    }
    const currentDate = year + '-' + month + '-' + day;
    return currentDate;
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

    let commitDate;
    do {
        commitDate = prompt(this.translate.instant('transaction.commitTransaction'), this.getCurrentDate());
        if (commitDate !== '' && commitDate !== null && !isNaN(new Date(commitDate).getTime()) && new Date(commitDate) <= new Date()) {
          transaction.date = new Date(commitDate);
          this.commit(transaction);
          break;
        } else if (new Date(commitDate) > new Date()) {
          alert(this.translate.instant('message.InvalidFutureDate'));
        } else if (commitDate === '' || commitDate === null) {
          alert(this.translate.instant('message.InvalidEmptyDate'));
          break;
        } else {
          alert(this.translate.instant('message.InvalidDate'));
        }
    }
    while (commitDate !== '' && commitDate !== null);
  }
  // TODO add dialog asking for date
  commitOverduePlannedTransaction(transaction: Transaction) {
    const commitDialogMessageKey = 'message.wantCommitPlannedTransactionBeforeDate';

    if (confirm(this.translate.instant(commitDialogMessageKey))) {
      this.commit(transaction, 'message.OverduePlannedTransactionCommitted');
    }
  }

  private commit(transaction: Transaction, messageKey?: string) {
    this.transactionService.commitPlannedTransaction(transaction)
        .subscribe((transactionsIdsFromResponse) => {
            this.alertService.success(
              this.translate.instant(messageKey == null ? 'message.plannedTransactionCommitted' : messageKey)
            );
            if (transactionsIdsFromResponse.savedTransactionId) {
              this.transactionService.getTransaction(transactionsIdsFromResponse.savedTransactionId)
                  .subscribe(
                    (saved) => {
                      this.updateCachedAccountBalanceAfterTransactionDeleted(transaction);
                      const savedTransaction = this.getTransactionFromResponse(saved);
                      this.removeDeletedFromDOM(transaction);
                      this.transactions.push(savedTransaction);
                      this.allTransactions.push(savedTransaction);
                      this.updateCachedAccountBalanceAfterTransactionAdded(savedTransaction);
                      this.calculateAndAssignPostTransactionBalances();
                    }
                  );
            }
            if (transactionsIdsFromResponse.recurrentTransactionId) {
              this.transactionService.getTransaction(transactionsIdsFromResponse.recurrentTransactionId)
                  .subscribe(
                    (recurrentTransaction) => {
                      const scheduledTransaction = this.getTransactionFromResponse(recurrentTransaction);
                      this.transactions.push(scheduledTransaction);
                      this.allTransactions.push(scheduledTransaction);
                      this.updateCachedAccountBalanceAfterTransactionAdded(scheduledTransaction);
                      this.calculateAndAssignPostTransactionBalances();
                    }
                  );
            }
          },
        );
  }

  private isEditingTransactionWithArchivedAccount(transaction: Transaction) {
    return this.containsArchivedAccount(transaction);
  }

  private getTransactionFromResponse(transactionResponse) {
    const transaction = new Transaction();
    transaction.date = transactionResponse.date;
    transaction.id = transactionResponse.id;
    transaction.description = transactionResponse.description;
    transaction.isPlanned = transactionResponse.planned;
    transaction.recurrencePeriod = transactionResponse.recurrencePeriod;
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

  setAsRecurrent(transaction: Transaction, recurrencePeriod: RecurrencePeriod) {
    this.transactionService.setAsRecurrent(transaction, recurrencePeriod)
      .subscribe(() => {
        if (recurrencePeriod === 'EVERY_DAY') {
          this.alertService.success(
            this.translate.instant('message.transactionSetRecurrentEveryDay'));
        } else if (recurrencePeriod === 'EVERY_WEEK') {
          this.alertService.success(
            this.translate.instant('message.transactionSetRecurrentEveryWeek'));
        } else if (recurrencePeriod === 'EVERY_MONTH') {
          this.alertService.success(
            this.translate.instant('message.transactionSetRecurrentEveryMonth'));
        }
        transaction.recurrencePeriod = recurrencePeriod;
      }
      );
  }

  setAsNotRecurrent(transaction: Transaction) {
    this.transactionService.setAsNotRecurrent(transaction, RecurrencePeriod.NONE)
        .subscribe(() => {
            this.alertService.success(
              this.translate.instant('message.transactionSetNotRecurrent'));
            transaction.recurrencePeriod = RecurrencePeriod.NONE;

          }
        );
  }

  private isTransactionDateCurrentDate(transaction) {
    const currentDate = this.pipe.transform(Date.now(), 'longDate');
    const transactionDate = this.pipe.transform(transaction.date, 'longDate');
    return transactionDate === currentDate;
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
        this.alertService.error(this.translate.instant('\'prompt.info.edit.containsArchivedAccount\''));
        status = false;
      }
    }

    return status;
  }

  onShowEditMode(transaction: Transaction) {
    transaction.editedTransaction = JSON.parse(JSON.stringify(transaction));
    transaction.editMode = true;

    if (this.containsArchivedAccount(transaction)) {
      alert(this.translate.instant('prompt.info.edit.containsArchivedAccount'));
    }

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

  private getStatusBasedBgColorForPlannedTransaction(transaction: any) {
    if (this.isOverduePlannedTransaction(transaction)) {
      return '#F1AD8D';
    }
    if (transaction.isPlanned) {
      return '#c7ffc0';
    }
  }

  private isOverduePlannedTransaction(transaction: any) {
    return transaction.isPlanned && DateHelper.isPastDate(transaction.date);
  }

  private isNotOverduePlannedTransaction(transaction: any) {
    return transaction.isPlanned && !DateHelper.isPastDate(transaction.date);
  }

  private isRecurrent(transaction: any): boolean {
    return !transaction.recurrencePeriod ? false : transaction.recurrencePeriod !== RecurrencePeriod.NONE;
  }

  isEditModeContainsArchivedAccount(transaction: any) {
    return !transaction.editMode || transaction.editMode && this.containsArchivedAccount(transaction);
  }

  negateHidePlannedTransactionsCheckboxAndSaveState() {
    const negatedState = !this.getHidePlannedTransactionsCheckboxState();
    sessionStorage.setItem('hidePlannedTransactionsCheckboxState', JSON.stringify(negatedState));
    return negatedState;
  }

  getHidePlannedTransactionsCheckboxState() {
    const checkBoxState = JSON.parse(sessionStorage.getItem('hidePlannedTransactionsCheckboxState'));
    return checkBoxState === null ? environment.hidePlannedTransactionsCheckboxStateOnApplicationStart : checkBoxState;
  }

   negatesetUpDefaultFilterCheckboxAndSaveState(){
     }

   getSetUpDefaultFilterCheckboxState(){
     }
}

