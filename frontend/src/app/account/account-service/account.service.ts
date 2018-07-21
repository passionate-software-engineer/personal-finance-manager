import {Injectable} from '@angular/core';
import {Account} from '../account';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable, of} from 'rxjs';
import {MessagesService} from '../../messages/messages.service';
import {catchError, tap} from 'rxjs/operators';

const httpOptions = {
  headers: new HttpHeaders({'Content-Type': 'application/json'})
};

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private apiUrl = 'http://localhost:8088/accounts';

  constructor(private http: HttpClient, private messagesService: MessagesService) {
  }

  getAccounts(): Observable<Account[]> {
    return this.http.get<Account[]>(this.apiUrl).pipe(
      tap(categories => this.log(`fetched accounts`)),
      catchError(this.handleError('getAccounts', [])));
  }

  addAccount(account: Account): Observable<any> {
    return this.http.post<any>(this.apiUrl, account, httpOptions).pipe(
      tap(any => this.log(`added account with id: ` + any)),
      catchError(this.handleError('addAccount', [])));
  }

  deleteAccount(id: number): Observable<any> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.delete<Account>(url).pipe(
      tap(() => this.log(`deleted account with id: ` + id)),
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

      alert(error.error);

      // TODO: send the error to remote logging infrastructure
      console.error(error); // log to console instead

      // TODO: better job of transforming error for user consumption
      this.log(`${operation} failed: ${error.message}  `);
      this.log(`${operation} failed: ${error.error}  `);

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }
}
