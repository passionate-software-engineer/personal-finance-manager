import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {Transaction} from '../Transaction';
import {MessagesService} from '../../messages/messages.service';
import {catchError, tap} from 'rxjs/operators';
import {environment} from '../../../environments/environment';
import {AlertsService} from '../../alerts/alerts-service/alerts.service';

const httpOptions = {
  headers: new HttpHeaders({'Content-Type': 'application/json'})
};

@Injectable({
  providedIn: 'root'
})
export class TransactionService {

  private apiUrl = environment.appUrl + '/categories';

  constructor(private http: HttpClient, private messagesService: MessagesService,
              private alertService: AlertsService) {
  }

  private static categoryToCategoryRequest(category: Transaction) {
    return {
      name: category.name,
      parentCategoryId: category.parentCategory == null ? null : category.parentCategory.id
    };
  }

  getTransactions(): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(this.apiUrl).pipe(
      tap(() => this.log(`fetched categories`)),
      catchError(this.handleError('getCategories', [])));
  }

  addTransaction(category: Transaction): Observable<any> {
    const categoryRequest = TransactionService.categoryToCategoryRequest(category);
    return this.http.post<any>(this.apiUrl, categoryRequest, httpOptions).pipe(
      tap(any => this.log(`added category with id: ` + any)),
      catchError(this.handleError('addCategory', [])));
  }

  deleteTransaction(id: number): Observable<any> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.delete<any>(url).pipe(
      tap(() => this.log(`deleted category with id: ` + id)),
      catchError(this.handleError('deleteCategory', [])));
  }

  editTransaction(category: Transaction): Observable<any> {
    const categoryRequest = TransactionService.categoryToCategoryRequest(category);
    const url = `${this.apiUrl}/${category.id}`;
    return this.http.put<Transaction>(url, categoryRequest, httpOptions).pipe(
      tap(() => this.log(`edited category with id: ` + category.id)),
      catchError(this.handleError('editCategory', [])));
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
