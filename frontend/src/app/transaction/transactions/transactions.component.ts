import {Component, OnInit} from '@angular/core';
import {TransactionService} from '../transaction-service/transaction.service';
import {AlertsService} from '../../alerts/alerts-service/alerts.service';
import {Transaction} from '../Transaction';
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
    this.getTransactions();
    this.getCategories();
    this.getAccounts();
  }

  getTransactions(): void {
    this.transactionService.getTransactions()
      .subscribe(transactions => {
        this.transactions = transactions;
      });
  }

  getCategories(): void {
    this.categoryService.getCategories()
      .subscribe(categories => {
        this.categories = categories;
      });
  }

  getAccounts(): void {
    this.accountService.getAccounts()
      .subscribe(accounts => {
        this.accounts = accounts;
      });
  }

  // TODO make nice looking confirmation popup

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
    if (!this.validateTransaction(transaction)) {
      return;
    }

    const editedTransaction: Transaction = new Transaction();
    editedTransaction.id = transaction.editedTransaction.id;
    editedTransaction.description = transaction.editedTransaction.description;
    editedTransaction.date = transaction.editedTransaction.date;
    editedTransaction.price = transaction.editedTransaction.price;
    editedTransaction.category = transaction.editedTransaction.category; // TODO send only category id
    editedTransaction.account = transaction.editedTransaction.account; // TODO send only account id

    this.transactionService.editTransaction(editedTransaction)
      .subscribe(() => {
        this.alertService.success('Transaction edited');
        this.transactionService.getTransaction(editedTransaction.id)
          .subscribe(updatedTransaction => {
          updatedTransaction.editMode = false;
          Object.assign(transaction, updatedTransaction);
        });
      });
  }

  onAddTransaction() {
    const transactionToAdd = JSON.parse(JSON.stringify(this.newTransaction));

    if (!this.validateTransaction(transactionToAdd)) {
      return;
    }

    this.transactionService.addTransaction(transactionToAdd)
      .subscribe(id => {
        this.alertService.success('Transaction added');
        this.transactionService.getTransaction(id)
          .subscribe(createdTransaction => {
            this.transactions.push(createdTransaction);
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
    if (transaction.date == null) {
      this.alertService.error('Date cannot be empty');
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
