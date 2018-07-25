import {Injectable} from '@angular/core';
import {Account} from '../account';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable, of, throwError} from 'rxjs';
import {MessagesService} from '../../messages/messages.service';
import {catchError, tap} from 'rxjs/operators';
import {environment} from '../../../environments/environment';
import {errorObject} from 'rxjs/internal-compatibility';

const httpOptions = {
  headers: new HttpHeaders({'Content-Type': 'application/json'})
};

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private apiUrl = environment.appUrl + '/accounts';

  constructor(private http: HttpClient, private messagesService: MessagesService) {
  }

  getAccounts(): Observable<Account[]> {
    console.log(this.apiUrl);
    return this.http.get<Account[]>(this.apiUrl).pipe(
      tap(() => this.log(`fetched accounts`)),
      catchError(this.handleError('getAccounts', [])));
  }

  addAccount(account: Account): Observable<any> {
    return this.http.post<any>(this.apiUrl, account, httpOptions).pipe(
      tap(any => {
        this.log(`added account with id: ` + any);
      }),
      catchError(this.handleError('addAccount', [])));
  }

  deleteAccount(id: number): Observable<any> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.delete<Account>(url).pipe(
      tap(() => {
        this.log(`deleted account with id: ` + id);
        Observable.throw(errorObject);
      }),
      catchError(this.handleError('deleteAccount', [])));
  }

  editAccount(account: Account): Observable<any> {
    const url = `${this.apiUrl}/${account.id}`;
    return this.http.put<Account>(url, account, httpOptions).pipe(
      tap(() => this.log(`edited category with id: ` + account.id)),
      catchError(this.handleError('editCategory', [])));
  }

  private log(message: string) {
    this.messagesService.add('AccountService: ' + message);
  }

  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {

      // TODO: better job of transforming error for user consumption
      this.log(`${operation} failed: ${error.message}  `);
      this.log(`${operation} failed: ${error.error}  `);

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }
}
