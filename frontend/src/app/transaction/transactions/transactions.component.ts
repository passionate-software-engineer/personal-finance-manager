///<reference path="../../../../node_modules/@angular/core/src/metadata/directives.d.ts"/>
import {Component, OnInit} from '@angular/core';
import {TransactionService} from '../transaction-service/transaction.service';
import {AlertsService} from '../../alerts/alerts-service/alerts.service';
import {Transaction} from '../Transaction';

@Component({
  selector: 'app-transactions',
  templateUrl: './transactions.component.html',
  styleUrls: ['./transactions.component.css']
})
export class TransactionsComponent implements OnInit {
  transactions: Transaction[];
  addingMode = false;
  newTransactionName: string;
  newTransactionParentCategory: Transaction = null;

  constructor(private categoryService: TransactionService, private alertService: AlertsService) {
  }

  ngOnInit() {
    this.getTransactions();
  }

  getTransactions(): void {
    this.categoryService.getTransactions()
      .subscribe(transactions => {
        this.transactions = transactions;
        this.sortByName('asc');
      });
  }

  // TODO make nice looking confirmation popup

  deleteTransaction(category) {
    if (confirm('Are you sure You want to delete this account ?')) {
      this.categoryService.deleteTransaction(category.id)
        .subscribe(() => {
          this.alertService.success('Transaction deleted');
          const index: number = this.transactions.indexOf(category);
          if (index !== -1) {
            this.transactions.splice(index, 1);
          }
        });
    }
  }

  onShowEditMode(category: Transaction) {
    category.editMode = true;
    category.editedName = category.name;
    if (category.parentCategory == null) {
      category.editedParentCategory = null;
    } else {
      category.editedParentCategory = <Transaction> JSON.parse(JSON.stringify(category.parentCategory));
    }

  }

  onEditTransaction(category: Transaction) {
    if (!this.validateTransaction(category.editedName)) {
      return;
    }
    const editedTransaction: Transaction = new Transaction();
    editedTransaction.id = category.id;
    editedTransaction.name = category.editedName;
    editedTransaction.parentCategory = category.editedParentCategory;
    this.categoryService.editTransaction(editedTransaction)
      .subscribe(() => {
        this.alertService.success('Transaction edited');
        Object.assign(category, editedTransaction);
        this.sortByName('asc');
      });
  }

  onAddTransaction() {
    const categoryToAdd = new Transaction();
    if (!this.validateAddingTransaction(this.newTransactionName)) {
      return;
    }
    categoryToAdd.name = this.newTransactionName;
    categoryToAdd.parentCategory = this.newTransactionParentCategory;
    this.categoryService.addTransaction(categoryToAdd)
      .subscribe(id => {
        categoryToAdd.id = id;
        this.transactions.push(categoryToAdd);
        this.alertService.success('Transaction added');
        this.addingMode = false;
        this.newTransactionName = null;
        this.newTransactionParentCategory = null;
        this.sortByName('asc');
      });
  }

  onRefreshTransactions() {
    this.getTransactions();
  }

  // TODO make sorting using pipes not methods below

  sortByName(type: string) {
    if (type === 'asc') {
      this.transactions.sort((a1, a2) => (a1.name.toLowerCase() > a2.name.toLowerCase() ? 1 : -1));
    }
    if (type === 'dsc') {
      this.transactions.sort((a1, a2) => (a1.name.toLowerCase() > a2.name.toLowerCase() ? -1 : 1));
    }
  }

  sortByParentTransaction(type: string) {
    if (type === 'asc') {
      this.transactions.sort((a1, a2) => {
        if (a1.parentCategory == null) {
          return 1;
        }
        if (a2.parentCategory == null) {
          return -1;
        }
        return a1.parentCategory.name.toLowerCase() > a2.parentCategory.name.toLowerCase() ? -1 : 1;
      });
    }
    if (type === 'dsc') {
      this.transactions.sort((a1, a2) => {
        if (a1.parentCategory == null) {
          return -1;
        }
        if (a2.parentCategory == null) {
          return 1;
        }
        return a1.parentCategory.name.toLowerCase() < a2.parentCategory.name.toLowerCase() ? -1 : 1;
      });
    }
  }

  getParentCategoryName(category): string {
    if (category.parentCategory != null) {
      return category.parentCategory.name;
    }
    return 'Main Category';
  }

  getListOfPossibleParentTransactions(cat: Transaction) {
    return this.transactions.filter(category => {
      if (category.id === cat.id) {
        return false;
      }
      let categoryToCheck = category.parentCategory;
      while (categoryToCheck != null) {

        if (categoryToCheck.id === cat.id) {
          return false;
        }
        categoryToCheck = categoryToCheck.parentCategory;
      }
      return true;
    });
  }

  validateTransaction(categoryName: string): boolean {
    if (categoryName == null || categoryName.trim() === '') {
      this.alertService.error('Transaction name cannot be empty');
      return false;
    }
    if (categoryName.length > 70) {
      this.alertService.error('Transaction name too long. Transaction name can not be longer then 100 characters');
      return false;
    }
    return true;
  }

  validateAddingTransaction(categoryName: string): boolean {
    if (!this.validateTransaction(categoryName)) {
      return false;
    }

    if (this.transactions.filter(category =>
      category.name.toLowerCase()
      === categoryName.toLowerCase()).length > 0) {
      this.alertService.error('Transaction with provided name already exist');
      return false;
    }
    return true;
  }

}
