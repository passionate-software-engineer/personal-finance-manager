///<reference path="../../../../node_modules/@angular/core/src/metadata/directives.d.ts"/>
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

  // // TODO make nice looking confirmation popup
  //
  // deleteTransaction(category) {
  //   if (confirm('Are you sure You want to delete this account ?')) {
  //     this.categoryService.deleteTransaction(category.id)
  //       .subscribe(() => {
  //         this.alertService.success('Transaction deleted');
  //         const index: number = this.transactions.indexOf(category);
  //         if (index !== -1) {
  //           this.transactions.splice(index, 1);
  //         }
  //       });
  //   }
  // }

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

    // transaction.editedName = transaction.description;
    // if (transaction.parentCategory == null) {
    //   transaction.editedParentCategory = null;
    // } else {
    //   transaction.editedParentCategory = <Transaction> JSON.parse(JSON.stringify(transaction.parentCategory));
    // }

  }

  onEditTransaction(transaction: Transaction) {
    // if (!this.validateTransaction(category.editedName)) {
    //   return;
    // }
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
        Object.assign(transaction, editedTransaction);
        // this.sortByName('asc');
      });
  }

  onAddTransaction() {
    const transactionToAdd = JSON.parse(JSON.stringify(this.newTransaction));

    // categoryToAdd.name = this.newTransactionName;
    // categoryToAdd.category = this.newTransactionParentCategory;

    this.transactionService.addTransaction(transactionToAdd)
      .subscribe(id => {
        transactionToAdd.id = id;
        this.transactions.push(transactionToAdd);
        this.alertService.success('Transaction added');
        this.addingMode = false;
        this.newTransaction = new Transaction();
      });
  }

  onRefreshTransactions() {
    this.getTransactions();
  }

  // TODO make sorting using pipes not methods below

  // sortByName(type: string) {
  //   if (type === 'asc') {
  //     this.transactions.sort((a1, a2) => (a1.name.toLowerCase() > a2.name.toLowerCase() ? 1 : -1));
  //   }
  //   if (type === 'dsc') {
  //     this.transactions.sort((a1, a2) => (a1.name.toLowerCase() > a2.name.toLowerCase() ? -1 : 1));
  //   }
  // }
  //
  // sortByParentTransaction(type: string) {
  //   if (type === 'asc') {
  //     this.transactions.sort((a1, a2) => {
  //       if (a1.category == null) {
  //         return 1;
  //       }
  //       if (a2.category == null) {
  //         return -1;
  //       }
  //       return a1.category.name.toLowerCase() > a2.category.name.toLowerCase() ? -1 : 1;
  //     });
  //   }
  //   if (type === 'dsc') {
  //     this.transactions.sort((a1, a2) => {
  //       if (a1.category == null) {
  //         return -1;
  //       }
  //       if (a2.category == null) {
  //         return 1;
  //       }
  //       return a1.category.name.toLowerCase() < a2.category.name.toLowerCase() ? -1 : 1;
  //     });
  //   }
  // }

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
      let categoryToCheck = category.category;
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
        category.description.toLowerCase()
        === categoryName.toLowerCase()).length > 0) {
      this.alertService.error('Transaction with provided name already exist');
      return false;
    }
    return true;
  }

}
