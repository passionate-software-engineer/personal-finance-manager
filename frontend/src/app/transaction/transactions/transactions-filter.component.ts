import {AlertsService} from '../../alerts/alerts-service/alerts.service';
import {Transaction} from '../transaction';
import {Category} from '../../category/category';
import {Account} from '../../account/account';
import {TransactionFilter} from '../transaction-filter';
import {TransactionFilterService} from '../transaction-filter-service/transaction-filter.service';
import {FilterResponse} from '../transaction-filter-service/transaction-filter-response';
import {Sortable} from '../../sortable';

export class FiltersComponentBase extends Sortable {
  allTransactions: Transaction[] = [];
  transactions: Transaction[] = [];
  categories: Category[] = [];
  accounts: Account[] = [];
  selectedFilter = new TransactionFilter();
  originalFilter = new TransactionFilter();
  filters: TransactionFilter[] = [];

  constructor(protected alertService: AlertsService, private filterService: TransactionFilterService) {
    super('date');
  }

  onFilterChange() {
    this.selectedFilter = JSON.parse(JSON.stringify(this.originalFilter));

    this.selectedFilter.accounts = [];
    for (const account of this.originalFilter.accounts) {
      this.selectedFilter.accounts.push(account);
    }

    this.selectedFilter.categories = [];
    for (const category of this.originalFilter.categories) {
      this.selectedFilter.categories.push(category);
    }

    this.filterTransactions();
  }

  getFilters(): void {
    this.filterService.getFilters()
      .subscribe(filters => {
        for (const filter of filters) {
          const processedFilter = this.processFilter(filter);
          this.filters.push(processedFilter);
        }
        this.setCurrentFilter();
      });
  }

  addFilter() {
    if (!this.validateFilter(this.selectedFilter)) {
      return;
    }

    this.filterService.addFilter(this.selectedFilter)
      .subscribe(id => {
        this.alertService.success('Filter added');
        this.filterService.getFilter(id)
          .subscribe(createdFilter => {
            const processedFilter = this.processFilter(createdFilter);
            this.filters.push(processedFilter);
            this.sortFilters();

            this.originalFilter = processedFilter;
            this.onFilterChange();
          });
      });
  }

  resetFilter() {
    this.onFilterChange();
  }

  updateFilter() {
    if (this.originalFilter.id === undefined) {
      this.alertService.warn('Filter "' + this.originalFilter.name + '" cannot be updated. Please create new filter instead.');
      return;
    }

    if (!this.validateFilter(this.selectedFilter, true)) {
      return;
    }

    this.filterService.updateFilter(this.selectedFilter)
      .subscribe(() => {
        this.alertService.success('Filter updated');
        this.filterService.getFilter(this.selectedFilter.id)
          .subscribe(createdFilter => {
            const processedFilter = this.processFilter(createdFilter);
            this.filters = this.filters.filter(filter => filter !== this.originalFilter);
            this.filters.push(processedFilter);
            this.sortFilters();

            this.originalFilter = processedFilter;
            this.onFilterChange();
          });
      });
  }

  deleteFilter() {
    if (this.originalFilter.id === undefined) {
      this.alertService.warn('Filter "' + this.originalFilter.name + '" cannot be deleted');
      return;
    }

    if (confirm('Are you sure You want to delete filter named "' + this.originalFilter.name + '"?')) {
      this.filterService.deleteFilter(this.originalFilter.id)
        .subscribe(() => {
          this.alertService.success('Filter deleted');
          this.filters = this.filters.filter(filter => filter !== this.originalFilter);

          this.setCurrentFilter();
        });
    }
  }

  private setCurrentFilter() {
    this.sortFilters();

    this.originalFilter = this.filters[0];
    this.onFilterChange();
  }

  protected addShowAllFilter() {
    if (this.filters.length === 0) {
      const newFilter = new TransactionFilter();
      newFilter.name = 'Show all';
      newFilter.categories = [];
      newFilter.accounts = [];

      this.filters.push(newFilter);
    }
  }

  private processFilter(filterResponse: FilterResponse): TransactionFilter {
    const filter = new TransactionFilter();
    filter.id = filterResponse.id;
    filter.name = filterResponse.name;
    filter.dateFrom = filterResponse.dateFrom;
    filter.dateTo = filterResponse.dateTo;
    filter.priceFrom = +filterResponse.priceFrom; // + added to convert to number
    filter.priceTo = +filterResponse.priceTo; // + added to convert to number
    filter.description = filterResponse.description;
    filter.accounts = [];
    filter.categories = [];

    for (const accountId of filterResponse.accountIds) {
      for (const account of this.accounts) {
        if (account.id === accountId) {
          filter.accounts.push(account);
        }
      }
    }

    for (const categoryId of filterResponse.categoryIds) {
      for (const category of this.categories) {
        if (category.id === categoryId) {
          filter.categories.push(category);
        }
      }
    }

    return filter;
  }

  sortFilters() {
    this.filters.sort(function (a, b) {
      if (a.name < b.name) {
        return -1;
      }
      if (a.name > b.name) {
        return 1;
      }
      return 0;
    });
  }

  protected filterTransactions() {
    this.transactions = [];
    for (const transaction of this.allTransactions) {
      this.transactions.push(transaction);
    }

    if (this.selectedFilter.priceFrom !== undefined && this.selectedFilter.priceFrom !== null) {
      this.transactions = this.transactions.filter(transaction => transaction.price >= this.selectedFilter.priceFrom);
    }

    if (this.selectedFilter.priceTo !== undefined && this.selectedFilter.priceTo !== null) {
      this.transactions = this.transactions.filter(transaction => transaction.price <= this.selectedFilter.priceTo);
    }

    if (this.selectedFilter.dateFrom !== undefined && this.selectedFilter.dateFrom !== null && this.selectedFilter.dateFrom !== '') {
      this.transactions = this.transactions
        .filter(transaction => new Date(transaction.date).getTime() >= new Date(this.selectedFilter.dateFrom).getTime());
    }

    if (this.selectedFilter.dateTo !== undefined && this.selectedFilter.dateTo !== null && this.selectedFilter.dateTo !== '') {
      this.transactions = this.transactions
        .filter(transaction => new Date(transaction.date).getTime() <= new Date(this.selectedFilter.dateTo).getTime());
    }

    if (this.selectedFilter.description !== undefined && this.selectedFilter.description !== null) {
      this.transactions = this.transactions
        .filter(transaction => transaction.description.toLowerCase().indexOf(this.selectedFilter.description.toLowerCase()) !== -1);
    }

    if (this.selectedFilter.accounts !== undefined && this.selectedFilter.accounts.length > 0) {
      this.transactions = this.transactions
        .filter(transaction => this.selectedFilter.accounts.indexOf(transaction.account) !== -1);
    }

    if (this.selectedFilter.categories !== undefined && this.selectedFilter.categories.length > 0) {
      const allCategories = this.getAllChildCategoriesIncludingParent(this.selectedFilter.categories);
      this.transactions = this.transactions
        .filter(transaction => allCategories.indexOf(transaction.category) !== -1);
    }
  }

  // TODO calculate once and keep in memory, it change only when we modify list of categories
  private getAllChildCategoriesIncludingParent(filterCategories: Category[]) {
    const allCategories = [];

    for (const filterCategory of filterCategories) {
      for (const category of this.categories) {
        let temporaryCategory = category;
        while (temporaryCategory !== undefined && temporaryCategory !== null) {
          if (temporaryCategory.id === filterCategory.id) {
            allCategories.push(category);
          }
          temporaryCategory = temporaryCategory.parentCategory;
        }
      }
    }

    return allCategories;
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

  validateFilter(filter: TransactionFilter, allowDuplicatedName = false): boolean {
    let status = true;

    if (filter.name == null || filter.name.trim() === '') {
      this.alertService.error('Filter name cannot be empty');
      status = false;
    }

    if (!allowDuplicatedName && filter.name != null) {
      for (const existingFilter of this.filters) {
        if (existingFilter.name.toLowerCase() === filter.name.toLowerCase()) {
          this.alertService.error('Filter name already exists. Do you want to update existing filter?');
          status = false;
          break;
        }
      }
    }

    if (filter.priceFrom != null && filter.priceTo != null && filter.priceFrom > filter.priceTo) {
      this.alertService.error('Filter "from price" cannot be higher then "to price"');
      status = false;
    }

    if (filter.dateFrom != null && filter.dateTo != null && new Date(filter.dateFrom).getTime() > new Date(filter.dateTo).getTime()) {
      this.alertService.error('Filter "from date" cannot be later then "to date"');
      status = false;
    }

    return status;
  }

}
