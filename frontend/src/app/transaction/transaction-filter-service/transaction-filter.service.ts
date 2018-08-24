import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {Transaction} from '../transaction';
import {MessagesService} from '../../messages/messages.service';
import {catchError, tap} from 'rxjs/operators';
import {environment} from '../../../environments/environment';
import {AlertsService} from '../../alerts/alerts-service/alerts.service';
import {FilterResponse} from './transaction-filter-response';
import {TransactionFilter} from '../transaction-filter';

const httpOptions = {
  headers: new HttpHeaders({'Content-Type': 'application/json'})
};

@Injectable({
  providedIn: 'root'
})
export class TransactionFilterService {

  private apiUrl = environment.appUrl + '/filters';

  constructor(private http: HttpClient, private messagesService: MessagesService,
              private alertService: AlertsService) {
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
    return this.http.get<FilterResponse[]>(this.apiUrl)
      .pipe(tap(() => this.log(`fetched filters`)),
        catchError(this.handleError('getFilters', [])));
  }

  getFilter(id: number): Observable<FilterResponse> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.get<FilterResponse>(url)
      .pipe(tap(() => this.log(`fetched filter with id ` + id)),
        catchError(this.handleError('getSingleFilter', null)));
  }

  addFilter(filter: TransactionFilter): Observable<any> {
    const filterRequest = TransactionFilterService.filterToFilterRequest(filter);
    return this.http.post<any>(this.apiUrl, filterRequest, httpOptions)
      .pipe(tap(any => this.log(`added transaction with id: ` + any)),
        catchError(this.handleError('addFilter', [])));
  }

  deleteFilter(id: number): Observable<any> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.delete<any>(url).pipe(
      tap(() => this.log(`deleted filter with id: ` + id)),
      catchError(this.handleError('deleteFilter', [])));
  }

  updateFilter(filter: TransactionFilter): Observable<any> {
    const categoryRequest = TransactionFilterService.filterToFilterRequest(filter);
    const url = `${this.apiUrl}/${filter.id}`;
    return this.http.put<Transaction>(url, categoryRequest, httpOptions)
      .pipe(tap(() => this.log(`edited filter with id: ` + filter.id)),
        catchError(this.handleError('updateFilter', [])));
  }

  private log(message: string) {
    this.messagesService.add('CategoryService: ' + message);
  }

  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      if (error.status === 400) {
        this.alertService.error(error.error);
      }
      if (error.status === 0 || error.status === 500) {
        this.alertService.error('Sth goes wrong, try again later');
      }
      // TODO: better job of transforming error for user consumption
      this.log(`${operation} failed: ${error.message}  `);
      this.log(`${operation} failed: ${JSON.stringify(error)}  `);

      return throwError(error);
    };
  }
}
