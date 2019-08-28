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
  selectedPlannedFilter = new TransactionFilter();
  originalFilter = new TransactionFilter();
  filters: TransactionFilter[] = [];

  constructor(protected alertService: AlertsService, private filterService: TransactionFilterService, public translate: TranslateService) {
    super('date', true);
  }

  onFilterChange() {
    this.selectedPlannedFilter = JSON.parse(JSON.stringify(this.originalFilter));

    this.selectedPlannedFilter.accounts = [];
    for (const account of this.originalFilter.accounts) {
      this.selectedPlannedFilter.accounts.push(account);
    }

    this.selectedPlannedFilter.categories = [];
    for (const category of this.originalFilter.categories) {
      this.selectedPlannedFilter.categories.push(category);
    }

    this.filterPlannedTransactions();
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
    if (!this.validateFilter(this.selectedPlannedFilter)) {
      return;
    }

    this.filterService.addFilter(this.selectedPlannedFilter)
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

    if (!this.validateFilter(this.selectedPlannedFilter, true)) {
      return;
    }

    this.filterService.updateFilter(this.selectedPlannedFilter)
        .subscribe(() => {
          this.alertService.success(this.translate.instant('message.filterUpdated'));
          this.filterService.getFilter(this.selectedPlannedFilter.id)
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

  protected filterPlannedTransactions() {
    this.transactions = [];
    for (const transaction of this.allTransactions) {
      this.transactions.push(transaction);
    }

    if (this.selectedPlannedFilter.priceFrom !== undefined && this.selectedPlannedFilter.priceFrom !== null) {
      this.transactions = this.transactions.filter(transaction => {
        for (const accountPriceEntry of transaction.accountPriceEntries) {
          if (accountPriceEntry.price >= this.selectedPlannedFilter.priceFrom) {
            return true;
          }
        }
        return false;
      });
    }

    if (this.selectedPlannedFilter.priceTo !== undefined && this.selectedPlannedFilter.priceTo !== null) {
      this.transactions = this.transactions.filter(transaction => {
        for (const accountPriceEntry of transaction.accountPriceEntries) {
          if (accountPriceEntry.price <= this.selectedPlannedFilter.priceTo) {
            return true;
          }
        }
        return false;
      });
    }

    if (this.selectedPlannedFilter.dateFrom !== undefined && this.selectedPlannedFilter.dateFrom !== null && this.selectedPlannedFilter.dateFrom !== '') {
      this.transactions = this.transactions
                              .filter(transaction => new Date(transaction.date).getTime() >= new Date(this.selectedPlannedFilter.dateFrom).getTime());
    }

    if (this.selectedPlannedFilter.dateTo !== undefined && this.selectedPlannedFilter.dateTo !== null && this.selectedPlannedFilter.dateTo !== '') {
      this.transactions = this.transactions
                              .filter(transaction => new Date(transaction.date).getTime() <= new Date(this.selectedPlannedFilter.dateTo).getTime());
    }

    if (this.selectedPlannedFilter.description !== undefined && this.selectedPlannedFilter.description !== null) {
      this.transactions = this.transactions
                              .filter(transaction => transaction.description.toLowerCase()
                                                                .indexOf(this.selectedPlannedFilter.description.toLowerCase()) !== -1);
    }

    if (this.selectedPlannedFilter.accounts !== undefined && this.selectedPlannedFilter.accounts.length > 0) {
      this.transactions = this.transactions
                              .filter(transaction => {
                                  for (const accountPriceEntry of transaction.accountPriceEntries) {
                                    if (this.selectedPlannedFilter.accounts.indexOf(accountPriceEntry.account) !== -1) {
                                      return true;
                                    }
                                  }
                                  return false;
                                }
                              )
      ;
    }

    if (this.selectedPlannedFilter.categories !== undefined && this.selectedPlannedFilter.categories.length > 0) {
      const allCategories = this.getAllChildCategoriesIncludingParent(this.selectedPlannedFilter.categories);
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
