import {AlertsService} from '../../alert/alerts-service/alerts.service';
import {Transaction} from '../transaction';
import {Category} from '../../category/category';
import {Account} from '../../account/account';
import {TransactionFilter} from '../transaction-filter';
import {TransactionFilterService} from '../transaction-filter-service/transaction-filter.service';
import {FilterResponse} from '../transaction-filter-service/transaction-filter-response';
import {Sortable} from '../../../helpers/sortable';
import {TranslateService} from '@ngx-translate/core';

export class FiltersComponentBase extends Sortable {
  allTransactions: Transaction[] = [];
  transactions: Transaction[] = [];
  categories: Category[] = [];
  accounts: Account[] = [];
  selectedFilter = new TransactionFilter();
  originalFilter = new TransactionFilter();
  filters: TransactionFilter[] = [];

  constructor(protected alertService: AlertsService, private filterService: TransactionFilterService, public translate: TranslateService) {
    super('date', true);
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
          this.alertService.success(this.translate.instant('message.filterAdded'));
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
      this.alertService.warn('Filter ' + this.originalFilter.name + this.translate.instant('message.filterCantUpdate'));
      return;
    }

    if (!this.validateFilter(this.selectedFilter, true)) {
      return;
    }

    this.filterService.updateFilter(this.selectedFilter)
        .subscribe(() => {
          this.alertService.success(this.translate.instant('message.filterUpdated'));
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
      this.alertService.warn('Filter "' + this.originalFilter.name + this.translate.instant('message.filterCantDelete'));
      return;
    }

    if (confirm(this.translate.instant('message.filterSureDelete') + this.originalFilter.name + '"?')) {
      this.filterService.deleteFilter(this.originalFilter.id)
          .subscribe(() => {
            this.alertService.success(this.translate.instant('message.filterDelete'));
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

      // TODO not handled well - needs to reload page for language change to take effect - find way to refresh immediately
      newFilter.name = this.translate.instant('filters.showAllFilterName');
      newFilter.categories = [];
      newFilter.accounts = [];

      this.filters.push(newFilter);

      this.originalFilter = newFilter;
      this.onFilterChange();
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
      if (a.name.toLowerCase() < b.name.toLowerCase()) {
        return -1;
      }
      if (a.name.toLowerCase() > b.name.toLowerCase()) {
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
      this.transactions = this.transactions.filter(transaction => {
        for (const accountPriceEntry of transaction.accountPriceEntries) {
          if (accountPriceEntry.price >= this.selectedFilter.priceFrom) {
            return true;
          }
        }
        return false;
      });
    }

    if (this.selectedFilter.priceTo !== undefined && this.selectedFilter.priceTo !== null) {
      this.transactions = this.transactions.filter(transaction => {
        for (const accountPriceEntry of transaction.accountPriceEntries) {
          if (accountPriceEntry.price <= this.selectedFilter.priceTo) {
            return true;
          }
        }
        return false;
      });
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
                              .filter(transaction => transaction.description.toLowerCase()
                                                                .indexOf(this.selectedFilter.description.toLowerCase()) !== -1);
    }

    if (this.selectedFilter.accounts !== undefined && this.selectedFilter.accounts.length > 0) {
      this.transactions = this.transactions
                              .filter(transaction => {
                                  for (const accountPriceEntry of transaction.accountPriceEntries) {
                                    if (this.selectedFilter.accounts.indexOf(accountPriceEntry.account) !== -1) {
                                      return true;
                                    }
                                  }
                                  return false;
                                }
                              )
      ;
    }

    if (this.selectedFilter.categories !== undefined && this.selectedFilter.categories.length > 0) {
      const allCategories = this.getAllChildCategoriesIncludingParent(this.selectedFilter.categories);
      this.transactions = this.transactions
                              .filter(transaction => allCategories.indexOf(transaction.category) !== -1);
    }
  }

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

  private validateFilter(filter: TransactionFilter, allowDuplicatedName = false): boolean {
    let status = true;

    if (filter.name == null || filter.name.trim() === '') {
      this.alertService.error(this.translate.instant('message.filterNameEmpty'));
      status = false;
    }

    if (!allowDuplicatedName && filter.name != null) {
      for (const existingFilter of this.filters) {
        if (existingFilter.name.toLowerCase() === filter.name.toLowerCase()) {
          this.alertService.error(this.translate.instant('message.filterNameExists'));
          status = false;
          break;
        }
      }
    }

    if (filter.priceFrom != null && filter.priceTo != null && filter.priceFrom > filter.priceTo) {
      this.alertService.error(this.translate.instant('message.filterWrongPriceRange'));
      status = false;
    }

    if (filter.dateFrom != null && filter.dateTo != null && new Date(filter.dateFrom).getTime() > new Date(filter.dateTo).getTime()) {
      this.alertService.error(this.translate.instant('message.filterWrongDateRange'));
      status = false;
    }

    return status;
  }

}
