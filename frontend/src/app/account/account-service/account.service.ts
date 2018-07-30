import {Injectable} from '@angular/core';
import {Account} from '../account';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
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
export class AccountService {
  private apiUrl = environment.appUrl + '/accounts';

  constructor(private http: HttpClient, private messagesService: MessagesService,
              private alertService: AlertsService) {
  }

  getAccounts(): Observable<Account[]> {
    return this.http.get<Account[]>(this.apiUrl)
      .pipe(
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
      }), catchError(this.handleError('deleteAccount', [])));
  }

  editAccount(account: Account): Observable<any> {
    const url = `${this.apiUrl}/${account.id}`;
    return this.http.put<Account>(url, account, httpOptions)
      .pipe(
        tap(() => this.log(`edited account with id: ` + account.id)),
        catchError(this.handleError('editAccount', [])));
  }

  private log(message: string) {
    this.messagesService.add('AccountService: ' + message);
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
