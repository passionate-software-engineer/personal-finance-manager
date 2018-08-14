import {Component, OnInit} from '@angular/core';
import {TransactionService} from '../transaction-service/transaction.service';
import {AlertsService} from '../../alerts/alerts-service/alerts.service';
import {Transaction} from '../transaction';
import {Account} from '../../account/account';
import {Category} from '../../category/category';
import {CategoryService} from '../../category/category-service/category.service';
import {AccountService} from '../../account/account-service/account.service';

@Component({
  selector: 'app-transactions',
  templateUrl: './transactions.component.html',
  styleUrls: ['./transactions.component.css']
})
export class TransactionsComponent implements OnInit {
  order = 'date';
  reverse = false;
  transactions: Transaction[];
  categories: Category[];
  accounts: Account[];
  addingMode = false;
  newTransaction = new Transaction();

  constructor(private transactionService: TransactionService, private alertService: AlertsService, private categoryService: CategoryService,
              private accountService: AccountService) {
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

    // TODO do in parallel with forkJoin (not working for some reason)
    this.categoryService.getCategories()
      .subscribe(categories => {
        this.categories = categories;

        this.accountService.getAccounts()
          .subscribe(accounts => {
            this.accounts = accounts;
            this.getTransactions();
          });
      });
  }

  getTransactions(): void {
    this.transactionService.getTransactions()
      .subscribe(transactions => {
        this.transactions = [];
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
        }
      });
  }

  deleteTransaction(transaction) {
    if (confirm('Are you sure You want to delete this transaction ?')) {
      this.transactionService.deleteTransaction(transaction.id)
        .subscribe(() => {
          this.alertService.success('Transaction deleted');
          const index: number = this.transactions.indexOf(transaction);
          if (index !== -1) {
            this.transactions.splice(index, 1);
          }
        });
    }
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

  onEditTransaction(transaction: Transaction) {
    if (!this.validateTransaction(transaction.editedTransaction)) {
      return;
    }

    this.transactionService.editTransaction(transaction.editedTransaction)
      .subscribe(() => {
        this.alertService.success('Transaction edited');
        this.transactionService.getTransaction(transaction.id)
          .subscribe(updatedTransaction => {
            const returnedTransaction = new Transaction();
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

  onAddTransaction() {
    if (!this.validateTransaction(this.newTransaction)) {
      return;
    }

    this.transactionService.addTransaction(this.newTransaction)
      .subscribe(id => {
        this.alertService.success('Transaction added');
        this.transactionService.getTransaction(id)
          .subscribe(createdTransaction => {

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
            this.addingMode = false;
            this.newTransaction = new Transaction();
          });
      });
  }

  onRefreshTransactions() {
    this.getTransactions();
  }

  validateTransaction(transaction: Transaction): boolean {
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

  setOrder(value: string) {
    if (this.order === value) {
      this.reverse = !this.reverse;
    }
    this.order = value;
  }
}
