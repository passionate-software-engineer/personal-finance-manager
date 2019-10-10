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

  title = 'Browser market shares at a specific website, 2014';
  type = 'BarChart';
  data = [];
  data_categories = [];
  columnNames = ['Browser', 'Percentage'];
  options = {
    bars: 'horizontal'
  };
  width = 1550;
  height = 1000;

  selectedMonth: Date;
  selectedCategory: number;
  costs = true;

  balanceOfAllTransactions = 0;
  incomeBalanceOfAllTransactions = 0;
  costBalanceOfAllTransactions = 0;
  incomeOfAllTransactionsInGivenMonth = [];
  costOfAllTransactionsInGivenMonth = [];
  balanceOfTransactionsInGivenMonth = [];
  balanceOfAllAccountsAtTheEndOfMonth = [];

  constructor(
    private categoryService: CategoryService,
    private alertService: AlertsService,
    private translate: TranslateService,
    private transactionService: TransactionService,
    private accountService: AccountService
  ) {
    this.calculateLast12Months();
    this.selectedMonth = this.last12Months[0];
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
                        this.categories[i].sumOfAllTransactionsInMonth = [];
                        this.categories[i].averageOfAllTransactions = this.categories[i].sumOfAllTransactions / this.last12Months.length;

                        for (let j = 0; j < this.last12Months.length; j++) {
                          this.categories[i].sumOfAllTransactionsInMonth.push(
                            this.getBalanceOfTransactionsInGivenCategoryAndMonth(this.categories[i].id, this.last12Months[j]));
                        }
                      }

                      this.balanceOfAllTransactions = this.getBalanceOfAllTransactions();
                      this.incomeBalanceOfAllTransactions = this.getIncomeBalanceOfAllTransactions();
                      this.costBalanceOfAllTransactions = this.getCostBalanceOfAllTransactions();

                      for (let i = 0; i < this.last12Months.length; i++) {
                        const month = this.last12Months[i];
                        this.incomeOfAllTransactionsInGivenMonth.push(this.getIncomeOfAllTransactionsInGivenMonth(month));
                        this.costOfAllTransactionsInGivenMonth.push(this.getCostOfAllTransactionsInGivenMonth(month));
                        this.balanceOfTransactionsInGivenMonth.push(this.getBalanceOfTransactionsInGivenMonth(month));
                        this.balanceOfAllAccountsAtTheEndOfMonth.push(this.getBalanceOfAllAccountsAtTheEndOfMonth(month));
                      }

                      this.updateMonthSpendingsChart(this.selectedMonth);

                      this.selectedCategory = categories[0].id;
                      this.updateCategorySpendingsChart(this.selectedCategory);
                    });
              });
        });
  }

  calculateLast12Months() {
    const today = new Date();
    for (let i = 0; i <= 12; ++i) {

      let year = today.getFullYear();
      let month = today.getMonth() - i;

      if (month < 0) {
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
  getAllTransactionsBalance(categoryId: number, onlyIncome: boolean = false, onlyCost: boolean = false): number {
    let sum = 0;

    for (let i = 0; i < this.transactions.length; ++i) {
      if (categoryId !== null && this.transactions[i].categoryId !== categoryId) {
        continue;
      }

      let sumOfAllAccountPriceEntries = 0;

      for (let j = 0; j < this.transactions[i].accountPriceEntries.length; ++j) {
        let exchangeRate = 1.0;
        for (const account of this.accounts) { // TODO use hashmap
          if (account.id === this.transactions[i].accountPriceEntries[j].accountId) {
            exchangeRate = +account.currency.exchangeRate;
          }
        }

        sumOfAllAccountPriceEntries += +this.transactions[i].accountPriceEntries[j].price * exchangeRate;

      }

      if ((onlyIncome && sumOfAllAccountPriceEntries > 0) || (onlyCost && sumOfAllAccountPriceEntries < 0) || (!onlyIncome && !onlyCost)) {
        sum += sumOfAllAccountPriceEntries;
      }
    }

    return sum;
  }

  getBalanceOfTransactionsInGivenCategoryAndMonth(
    categoryId: number,
    beginningOfMonth: Date,
    onlyIncome: boolean = false,
    onlyCost: boolean = false
  ): number {
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

      let sumOfAllAccountPriceEntries = 0;

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

        sumOfAllAccountPriceEntries += +this.transactions[i].accountPriceEntries[j].price * exchangeRate;
      }

      if ((onlyIncome && sumOfAllAccountPriceEntries > 0) || (onlyCost && sumOfAllAccountPriceEntries < 0) || (!onlyIncome && !onlyCost)) {
        sum += sumOfAllAccountPriceEntries;
      }
    }

    return sum;
  }

  getBalanceOfTransactionsInGivenMonth(beginningOfMonth: Date) {
    return this.getBalanceOfTransactionsInGivenCategoryAndMonth(null, beginningOfMonth);
  }

  getIncomeOfAllTransactionsInGivenMonth(beginningOfMonth: Date) {
    return this.getBalanceOfTransactionsInGivenCategoryAndMonth(null, beginningOfMonth, true);
  }

  getCostOfAllTransactionsInGivenMonth(beginningOfMonth: Date) {
    return this.getBalanceOfTransactionsInGivenCategoryAndMonth(null, beginningOfMonth, false, true);
  }

  getBalanceOfAllTransactions() {
    return this.getAllTransactionsBalance(null);
  }

  getIncomeBalanceOfAllTransactions() {
    return this.getAllTransactionsBalance(null, true);
  }

  getCostBalanceOfAllTransactions() {
    return this.getAllTransactionsBalance(null, false, true);
  }

  private balanceOfAllAccounts() {
    let sum = 0;

    for (let i = 0; i < this.accounts.length; ++i) {
      sum +=
        +this.accounts[i].balance * +this.accounts[i].currency.exchangeRate;
    }

    return sum;
  }

  private getCanonicalFormForDate(date: Date) {
    return 100 * date.getFullYear() + date.getMonth() + 1;
  }

  getBalanceOfAllAccountsAtTheEndOfMonth(beginningOfMonth: Date) {
    let accountsBalance = this.balanceOfAllAccounts();
    let processedMonth = new Date();
    processedMonth = new Date(processedMonth.getFullYear(), processedMonth.getMonth(), 1);

    let processedMonthInCananicalFormat = this.getCanonicalFormForDate(processedMonth);
    const beginningOfMonthInCanonicalFormat = this.getCanonicalFormForDate(beginningOfMonth);

    while (processedMonthInCananicalFormat > beginningOfMonthInCanonicalFormat) {
      accountsBalance -= this.getBalanceOfTransactionsInGivenMonth(processedMonth);

      let year = processedMonth.getFullYear();
      let month = processedMonth.getMonth() - 1;

      if (month < 0) {
        month += 12;
        year -= 1;
      }

      processedMonth.setMonth(month);
      processedMonth.setFullYear(year);

      processedMonthInCananicalFormat = this.getCanonicalFormForDate(processedMonth);
    }

    return accountsBalance;
  }

  Comparator(a, b) {
    if (a[1] < b[1]) {
      return -1;
    }
    if (a[1] > b[1]) {
      return 1;
    }
    return 0;
  }

  updateMonthSpendingsChart(selectedMonth, costs = true) {
    this.data = [];
    for (let i = 0; i < this.categories.length; i++) {
      const sumOfTransactions = this.getBalanceOfTransactionsInGivenCategoryAndMonth(this.categories[i].id, selectedMonth);
      if (costs && sumOfTransactions < 0) {
        this.data.push([this.categories[i].name, -sumOfTransactions]);
      } else if (!costs && sumOfTransactions > 0) { // income
        this.data.push([this.categories[i].name, sumOfTransactions]);
      }
    }
    this.data.sort(this.Comparator);

    this.data = Object.assign([], this.data);
  }

  updateCategorySpendingsChart(selectedCategoryId) {
    this.data_categories = [];
    for (let i = 0; i < this.last12Months.length; i++) {
      const sumOfTransactions = this.getBalanceOfTransactionsInGivenCategoryAndMonth(selectedCategoryId, this.last12Months[i]);
      this.data_categories.push([this.last12Months[i], -sumOfTransactions]);
    }

    this.data_categories = Object.assign([], this.data_categories);
  }

  monthChanged(selectedMonth) {
    this.updateMonthSpendingsChart(selectedMonth, this.costs);
  }

  costsChanged(selectedCostsValue) {
    this.updateMonthSpendingsChart(this.selectedMonth, selectedCostsValue);
  }

  categoryChanged(selectedCategoryId) {
    this.updateCategorySpendingsChart(selectedCategoryId);
  }

}
