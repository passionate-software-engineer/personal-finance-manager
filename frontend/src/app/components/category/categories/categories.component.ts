import {TransactionService} from './../../transaction/transaction-service/transaction.service';
import {Component, OnInit} from '@angular/core';
import {Category} from '../category';
import {CategoryService} from '../category-service/category.service';
import {AlertsService} from '../../alert/alerts-service/alerts.service';
import {Sortable} from '../../../helpers/sortable';
import {TranslateService} from '@ngx-translate/core';
import {TransactionResponse} from '../../transaction/transaction-service/transaction-response';
import {AccountService} from '../../account/account-service/account.service';
import {Account} from '../../account/account';

@Component({ // TODO categories in dropdows should display with parent category e.g. Car > Parts (try using filter for it)
  selector: 'app-categories',
  templateUrl: './categories.component.html',
  styleUrls: ['./categories.component.css']
})
export class CategoriesComponent implements OnInit {
  categories: Category[] = [];
  accounts: Account[] = [];
  addingMode = false;
  newCategory: Category = new Category();
  transactions: TransactionResponse[] = [];
  last12Months: Date[] = [];
  sortableCategoriesTable: Sortable = new Sortable('name');
  sortableSummaryTable: Sortable = new Sortable('name');

  constructor(
    private categoryService: CategoryService,
    private alertService: AlertsService,
    private translate: TranslateService,
    private transactionService: TransactionService,
    private accountService: AccountService
  ) {
    this.calculateLast12Months();
  }

  ngOnInit() {
    this.categoryService.getCategories()
      .subscribe(categories => {
        this.categories = categories;

        this.accountService.getAccounts()
          .subscribe(accounts => {
            this.accounts = accounts;
            this.transactionService.getTransactions()
              .subscribe(transactions => {
                this.transactions = transactions;
                for (let i = 0; i < this.categories.length; i++) {
                  this.categories[i].sumOfAllTransactions = this.getAllTransactionsBalance(this.categories[i].id);
                }
              });
          });
      });
  }

  calculateLast12Months() {
    const today = new Date();
    for (let i = 0; i <= today.getMonth() + 12; ++i) {

      let year = today.getFullYear();
      let month = today.getMonth() - i;

      if (month < 1) {
        month += 12;
        year -= 1;
      }

      this.last12Months.push(new Date(year, month, 1));
    }
  }

  deleteCategory(category) {
    if (confirm(this.translate.instant('message.wantDeleteCategory'))) {
      this.categoryService.deleteCategory(category.id)
        .subscribe(() => {
          this.alertService.success(this.translate.instant('message.categoryDeleted'));
          const index: number = this.categories.indexOf(category);
          if (index !== -1) {
            this.categories.splice(index, 1);
          }
        });
    }
  }

  onShowEditMode(category: Category) {
    category.editMode = true;
    category.editedCategory = new Category();
    category.editedCategory.id = category.id;
    category.editedCategory.name = category.name;

    if (category.parentCategory != null) {
      category.editedCategory.parentCategory = category.parentCategory;

      // TODO that should not be needed if value in category is set correctly
      for (const categoryEntry of this.categories) {
        if (categoryEntry.id === category.editedCategory.parentCategory.id) {
          category.editedCategory.parentCategory = categoryEntry;
        }
      }
    }
  }

  onEditCategory(category: Category) {
    if (!this.validateCategory(category.editedCategory.name)) {
      return;
    }

    this.categoryService.editCategory(category.editedCategory)
      .subscribe(() => {
        this.alertService.success(this.translate.instant('message.categoryEdited'));
        Object.assign(category, category.editedCategory);
        category.editedCategory = new Category();
        // TODO get category from server
      });
  }

  onAddCategory() {
    if (!this.validateAddingCategory(this.newCategory.name)) {
      return;
    }

    this.categoryService.addCategory(this.newCategory)
      .subscribe(id => {
        this.newCategory.id = id;
        this.categories.push(this.newCategory);
        this.newCategory = new Category();
        this.alertService.success(this.translate.instant('message.categoryAdded'));
        this.addingMode = false;

        // TODO get category from server
      });
  }

  onRefreshCategories() {
    this.ngOnInit();
  }

  getParentCategoryName(category): string {
    if (category.parentCategory != null) {
      return category.parentCategory.name;
    }
    return this.translate.instant('category.mainCategory');
  }

  getListOfPossibleParentCategories(cat: Category) {
    return this.categories.filter(category => {
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

  validateCategory(categoryName: string): boolean { // TODO pass category object
    if (categoryName == null || categoryName.trim() === '') {
      this.alertService.error(this.translate.instant('message.categoryNameEmpty'));
      return false; // TODO validate all - not break on first failure
    }
    if (categoryName.length > 100) {
      this.alertService.error((this.translate.instant('message.categoryTooLong')));
      return false;
    }
    return true;
  }

  validateAddingCategory(categoryName: string): boolean {
    if (!this.validateCategory(categoryName)) {
      return false;
    }

    if (this.categories.filter(category => category.name.toLowerCase() === categoryName.toLowerCase()).length > 0) {
      this.alertService.error(this.translate.instant('message.categoryNameExists'));
      return false;
    }
    return true;
  }

  // TODO parent category should sum values of child categories
  getAllTransactionsBalance(categoryId: number): number {
    let sum = 0;

    for (let i = 0; i < this.transactions.length; ++i) {
      if (categoryId !== null && this.transactions[i].categoryId !== categoryId) {
        continue;
      }
      for (let j = 0; j < this.transactions[i].accountPriceEntries.length; ++j) {
        let exchangeRate = 1.0;
        for (const account of this.accounts) { // TODO use hashmap
          if (account.id === this.transactions[i].accountPriceEntries[j].accountId) {
            exchangeRate = +account.currency.exchangeRate;
          }
        }


        sum += +this.transactions[i].accountPriceEntries[j].price * exchangeRate;
      }
    }

    return sum;
  }

  getBalanceOfTransactionsInGivenCategoryAndMonth(categoryId: number, beginningOfMonth: Date): number {
    let sum = 0;

    let year = beginningOfMonth.getFullYear();
    let month = beginningOfMonth.getMonth() + 1;
    if (month > 12) {
      month -= 12;
      year += 1;
    }

    const beginningOfNextMonth = new Date(year, month, 1);


    for (let i = 0; i < this.transactions.length; ++i) {
      if (categoryId !== null && this.transactions[i].categoryId !== categoryId) {
        continue;
      }
      for (let j = 0; j < this.transactions[i].accountPriceEntries.length; ++j) {
        const transactionDate = new Date(this.transactions[i].date);
        if (transactionDate < beginningOfMonth || transactionDate >= beginningOfNextMonth) {
          continue;
        }

        let exchangeRate = 1.0;
        for (const account of this.accounts) { // TODO use hashmap
          if (account.id === this.transactions[i].accountPriceEntries[j].accountId) {
            exchangeRate = +account.currency.exchangeRate;
          }
        }


        sum += +this.transactions[i].accountPriceEntries[j].price * exchangeRate;
      }
    }

    return sum;
  }

  getBalanceOfTransactionsInGivenMonth(beginningOfMonth: Date) {
    return this.getBalanceOfTransactionsInGivenCategoryAndMonth(null, beginningOfMonth);
  }

  getBalanceOfAllTransactions() {
    return this.getAllTransactionsBalance(null);
  }
}
