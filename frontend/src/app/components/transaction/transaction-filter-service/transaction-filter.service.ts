import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Transaction} from '../transaction';
import {catchError} from 'rxjs/operators';
import {AlertsService} from '../../alert/alerts-service/alerts.service';
import {FilterResponse} from './transaction-filter-response';
import {TransactionFilter} from '../transaction-filter';
import {ServiceBase} from '../../../helpers/service-base';

const PATH = 'filters';

@Injectable({
  providedIn: 'root'
})
export class TransactionFilterService extends ServiceBase {

  constructor(http: HttpClient, alertService: AlertsService) {
    super(http, alertService);
  }

  private static filterToFilterRequest(filter: TransactionFilter) {
    const accounts = [];
    for (const account of filter.accounts) {
      accounts.push(account.id);
    }

    const categories = [];
    for (const category of filter.categories) {
      categories.push(category.id);
    }

    return {
      name: filter.name,
      description: filter.description,
      priceFrom: filter.priceFrom,
      priceTo: filter.priceTo,
      dateFrom: filter.dateFrom,
      dateTo: filter.dateTo,
      accountIds: accounts,
      categoryIds: categories
    };
  }

  getFilters(): Observable<FilterResponse[]> {
    return this.http.get<FilterResponse[]>(ServiceBase.apiUrl(PATH), this.httpCorrelationId)
      .pipe(catchError(this.handleError('getFilters', [])));
  }

  getFilter(id: number): Observable<FilterResponse> {
    return this.http.get<FilterResponse>(ServiceBase.apiUrl(PATH, id), this.httpCorrelationId)
      .pipe(catchError(this.handleError('getSingleFilter', null)));
  }

  addFilter(filter: TransactionFilter): Observable<any> {
    const filterRequest = TransactionFilterService.filterToFilterRequest(filter);
    return this.http.post<any>(ServiceBase.apiUrl(PATH), filterRequest, this.httpOptions)
      .pipe(catchError(this.handleError('addFilter', [])));
  }

  deleteFilter(id: number): Observable<any> {
    return this.http.delete<any>(ServiceBase.apiUrl(PATH, id), this.httpCorrelationId)
      .pipe(catchError(this.handleError('deleteFilter', [])));
  }

  updateFilter(filter: TransactionFilter): Observable<any> {
    const categoryRequest = TransactionFilterService.filterToFilterRequest(filter);
    return this.http.put<Transaction>(ServiceBase.apiUrl(PATH, filter.id), categoryRequest, this.httpOptions)
      .pipe(catchError(this.handleError('updateFilter', [])));
  }
}
