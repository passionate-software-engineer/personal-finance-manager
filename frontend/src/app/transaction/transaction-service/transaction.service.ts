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

  private apiUrl = environment.appUrl + '/transactions';

  constructor(private http: HttpClient, private messagesService: MessagesService,
              private alertService: AlertsService) {
  }

  private static transactionToTransactionRequest(transaction: Transaction) {
    // return {
    //   name: transaction.description,
    //   parentCategoryId: transaction.category == null ? null : transaction.category.id
    // };
    return transaction;
  }

  getTransactions(): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(this.apiUrl).pipe(
      tap(() => this.log(`fetched transactions`)),
      catchError(this.handleError('getTransactions', [])));
  }

  getTransaction(id: number): Observable<Transaction> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.get<Transaction>(url).pipe(
      tap(() => this.log(`fetched transaction with id ` + id)),
      catchError(this.handleError('getSingleTransaction', null)));
  }

  addTransaction(transaction: Transaction): Observable<any> {
    const categoryRequest = TransactionService.transactionToTransactionRequest(transaction);
    return this.http.post<any>(this.apiUrl, categoryRequest, httpOptions).pipe(
      tap(any => this.log(`added transaction with id: ` + any)),
      catchError(this.handleError('addTransaction', [])));
  }

  deleteTransaction(id: number): Observable<any> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.delete<any>(url).pipe(
      tap(() => this.log(`deleted transaction with id: ` + id)),
      catchError(this.handleError('deleteTransaction', [])));
  }

  editTransaction(category: Transaction): Observable<any> {
    const categoryRequest = TransactionService.transactionToTransactionRequest(category);
    const url = `${this.apiUrl}/${category.id}`;
    return this.http.put<Transaction>(url, categoryRequest, httpOptions).pipe(
      tap(() => this.log(`edited transaction with id: ` + category.id)),
      catchError(this.handleError('editTransaction', [])));
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
