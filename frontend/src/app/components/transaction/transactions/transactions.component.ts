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

@Component({
  selector: 'app-transactions',
  templateUrl: './transactions.component.html',
  styleUrls: ['./transactions.component.css']
})
export class TransactionsComponent extends FiltersComponentBase implements OnInit {
  allTransactions: Transaction[] = [];
  transactions: Transaction[] = [];
  categories: Category[] = [];
  accounts: Account[] = [];
  addingMode = false;
  newTransaction = new Transaction();
  selectedFilter = new TransactionFilter();
  originalFilter = new TransactionFilter();
  filters: TransactionFilter[] = [];

  constructor(private transactionService: TransactionService, alertService: AlertsService, private categoryService: CategoryService,
              private accountService: AccountService, filterService: TransactionFilterService, translate: TranslateService) {
    super(alertService, filterService, translate);
  }

  ngOnInit() {
    // forkJoin(
    //   this.categoryService.getCategories(),
    //   this.accountService.getAccounts(),
    //   (categories, accounts) => {
    //     this.categories = categories;
    //     this.accounts = accounts;
    //     return this.getTransactions();
    //   }
    // );

    this.filters = [];
    this.addShowAllFilter();

    // TODO do in parallel with forkJoin (not working for some reason)
    this.categoryService.getCategories()
      .subscribe(categories => {
        this.categories = categories;

        this.accountService.getAccounts()
          .subscribe(accounts => {
            this.accounts = accounts;
            this.getTransactions();
            this.getFilters();
          });
      });

    this.newTransaction.accountPriceEntries.push(new AccountPriceEntry());
    this.newTransaction.accountPriceEntries.push(new AccountPriceEntry()); // TODO add option to add multiple entries dynamically
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

          for (const entry of transactionResponse.accountPriceEntries) {
            const accountPriceEntry = new AccountPriceEntry();
            accountPriceEntry.price = +entry.price; // + added to convert to number

            // need to have same object to allow dropdown to work correctly
            for (const account of this.accounts) { // TODO use hashmap
              if (account.id === entry.accountId) {
                accountPriceEntry.account = account;
              }
            }

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

        super.filterTransactions();
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
            const returnedTransaction = new Transaction(); // TODO dupliated code
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
        this.alertService.success(this.translate.instant('message.transactionAdded'));
        this.transactionService.getTransaction(id)
          .subscribe(createdTransaction => {

            // TODO duplicate with above method
            const returnedTransaction = new Transaction();
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

    if (transaction.accountPriceEntries[0].price == null) {
      this.alertService.error(this.translate.instant('message.priceEmpty'));
      status = false;
    }

    if (transaction.category == null) {
      this.alertService.error(this.translate.instant('message.categoryNameEmpty'));
      status = false;
    }

    if (transaction.accountPriceEntries[0].account == null) {
      this.alertService.error(this.translate.instant('message.accountNameEmpty'));
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

    for (const category of this.categories) {
      if (category.id === transaction.editedTransaction.category.id) {
        transaction.editedTransaction.category = category;
      }
    }

  }


}
