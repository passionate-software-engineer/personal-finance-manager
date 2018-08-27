import {Component, OnInit} from '@angular/core';
import {TransactionService} from '../transaction-service/transaction.service';
import {AlertsService} from '../../alerts/alerts-service/alerts.service';
import {Transaction} from '../transaction';
import {Account} from '../../account/account';
import {Category} from '../../category/category';
import {CategoryService} from '../../category/category-service/category.service';
import {AccountService} from '../../account/account-service/account.service';
import {TransactionFilter} from '../transaction-filter';
import {TransactionFilterService} from '../transaction-filter-service/transaction-filter.service';
import {FiltersComponentBase} from './transactions-filter.component';

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
              private accountService: AccountService, filterService: TransactionFilterService) {
    super(alertService, filterService);
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
          transaction.price = +transactionResponse.price; // + added to convert to number
          transaction.description = transactionResponse.description;

          // need to have same object to allow dropdown to work correctly
          for (const account of this.accounts) {
            if (account.id === transactionResponse.accountId) {
              transaction.account = account;
            }
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
    if (confirm('Are you sure You want to delete this transaction ?')) {
      this.transactionService.deleteTransaction(transactionToDelete.id)
        .subscribe(() => {
          this.alertService.success('Transaction deleted');
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
        this.alertService.success('Transaction edited');
        this.transactionService.getTransaction(transaction.id)
          .subscribe(updatedTransaction => {
            const returnedTransaction = new Transaction(); // TODO dupliated code
            returnedTransaction.date = updatedTransaction.date;
            returnedTransaction.id = updatedTransaction.id;
            returnedTransaction.price = +updatedTransaction.price; // + added to convert to number
            returnedTransaction.description = updatedTransaction.description;

            // need to have same object to allow dropdown to work correctly
            for (const account of this.accounts) {
              if (account.id === updatedTransaction.accountId) {
                returnedTransaction.account = account;
              }
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
        this.alertService.success('Transaction added');
        this.transactionService.getTransaction(id)
          .subscribe(createdTransaction => {

            // TODO duplicate with above method
            const returnedTransaction = new Transaction();
            returnedTransaction.date = createdTransaction.date;
            returnedTransaction.id = createdTransaction.id;
            returnedTransaction.price = +createdTransaction.price; // + added to convert to number
            returnedTransaction.description = createdTransaction.description;

            // need to have same object to allow dropdown to work correctly
            for (const account of this.accounts) {
              if (account.id === createdTransaction.accountId) {
                returnedTransaction.account = account;
              }
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
      this.alertService.error('Date is empty or incomplete');
      status = false;
    }

    if (transaction.description == null || transaction.description.trim() === '') {
      this.alertService.error('Description cannot be empty');
      status = false;
    }

    if (transaction.description != null && transaction.description.length > 100) {
      this.alertService.error('Category name too long. Category name can not be longer then 100 characters');
      status = false;
    }

    if (transaction.price == null) {
      this.alertService.error('Price is empty or price format is incorrect');
      status = false;
    }

    if (transaction.category == null) {
      this.alertService.error('Category cannot be empty');
      status = false;
    }

    if (transaction.account == null) {
      this.alertService.error('Account cannot be empty');
      status = false;
    }

    return status;
  }

  onShowEditMode(transaction: Transaction) {
    transaction.editedTransaction = JSON.parse(JSON.stringify(transaction));
    transaction.editMode = true;

    // need to have same object to allow dropdown to work correctly
    for (const account of this.accounts) {
      if (account.id === transaction.editedTransaction.account.id) {
        transaction.editedTransaction.account = account;
      }
    }

    for (const category of this.categories) {
      if (category.id === transaction.editedTransaction.category.id) {
        transaction.editedTransaction.category = category;
      }
    }

  }


}
