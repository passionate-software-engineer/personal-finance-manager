import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Transaction} from '../transaction';
import {FilterResponse} from './transaction-filter-response';
import {TransactionFilter} from '../transaction-filter';
import {ServiceBase} from '../../../helpers/service-base';

const PATH = 'filters';

@Injectable({
  providedIn: 'root'
})
export class TransactionFilterService extends ServiceBase {

  constructor(http: HttpClient) {
    super(http);
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
    return this.http.get<FilterResponse[]>(ServiceBase.apiUrl(PATH));
  }

  getFilter(id: number): Observable<FilterResponse> {
    return this.http.get<FilterResponse>(ServiceBase.apiUrl(PATH, id));
  }

  addFilter(filter: TransactionFilter): Observable<any> {
    const filterRequest = TransactionFilterService.filterToFilterRequest(filter);
    return this.http.post<any>(ServiceBase.apiUrl(PATH), filterRequest, this.contentType);
  }

  deleteFilter(id: number): Observable<any> {
    return this.http.delete<any>(ServiceBase.apiUrl(PATH, id));
  }

  updateFilter(filter: TransactionFilter): Observable<any> {
    const categoryRequest = TransactionFilterService.filterToFilterRequest(filter);
    return this.http.put<Transaction>(ServiceBase.apiUrl(PATH, filter.id), categoryRequest, this.contentType);
  }
}
