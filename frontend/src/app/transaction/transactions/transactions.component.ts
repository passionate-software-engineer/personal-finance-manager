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
import {FilterResponse} from '../transaction-filter-service/transaction-filter-response';

@Component({
  selector: 'app-transactions',
  templateUrl: './transactions.component.html',
  styleUrls: ['./transactions.component.css']
})
export class TransactionsComponent implements OnInit {
  order = 'date';
  reverse = false;
  allTransactions: Transaction[];
  transactions: Transaction[];
  categories: Category[];
  accounts: Account[];
  addingMode = false;
  newTransaction = new Transaction();
  selectedFilter = new TransactionFilter();
  originalFilter = new TransactionFilter();
  filters: TransactionFilter[];

  constructor(private transactionService: TransactionService, private alertService: AlertsService, private categoryService: CategoryService,
              private accountService: AccountService, private filterService: TransactionFilterService) {
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

        this.filterTransactions();
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
    // if (!this.validateTransaction(this.newTransaction)) {
    //   return;
    // } // TODO validation

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

  updateFilter() {
    if (this.originalFilter.id === undefined) {
      this.alertService.warn('Filter "' + this.originalFilter.name + '" cannot be updated. Please create new instead.');
      return;
    }

    // if (!this.validateTransaction(this.newTransaction)) {
    //   return;
    // } // TODO validation

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

  private addShowAllFilter() {
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

  private filterTransactions() {
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

    if (this.selectedFilter.dateFrom !== undefined && this.selectedFilter.dateFrom !== null) {
      this.transactions = this.transactions.filter(transaction => transaction.date >= this.selectedFilter.dateFrom);
    }

    if (this.selectedFilter.dateTo !== undefined && this.selectedFilter.dateTo !== null) {
      this.transactions = this.transactions.filter(transaction => transaction.date <= this.selectedFilter.dateTo);
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
      const allCategories = this.getAllChildCategoriesIncludingParent(this.selectedFilter.categories[0]);
      this.transactions = this.transactions
        .filter(transaction => allCategories.indexOf(transaction.category) !== -1);
    }
  }

  // TODO calculate once and keep in memory, it change only when we modify list of categories
  private getAllChildCategoriesIncludingParent(filterCategory: Category) {
    const allCategories = [];

    for (const category of this.categories) {
      let temporaryCategory = category;
      while (temporaryCategory !== undefined && temporaryCategory !== null) {
        if (temporaryCategory.id === filterCategory.id) {
          allCategories.push(category);
        }
        temporaryCategory = temporaryCategory.parentCategory;
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

  setOrder(value: string) {
    if (this.order === value) {
      this.reverse = !this.reverse;
    }

    this.order = value;
  }

}
