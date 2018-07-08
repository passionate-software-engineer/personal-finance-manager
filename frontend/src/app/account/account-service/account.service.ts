import { Injectable } from '@angular/core';
import { Account } from '../account';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { MessagesService } from '../../messages/messages.service';
import { catchError, tap } from 'rxjs/operators';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};
@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private apiUrl = 'http://localhost:8081/accounts';

  constructor(private http: HttpClient, private messagesService: MessagesService) { }

  getAccounts(): Observable<Account[]> {
    return this.http.get<Account[]>(this.apiUrl);
  }

  addAccount(account: Account): Observable<any> {
    return this.http.post<any>(this.apiUrl, account, httpOptions);
  }

  deleteAccount(id: number): Observable<Account> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.delete<Account>(url);
  }

  editAccount(account: Account): Observable<Account> {
    const url = `${this.apiUrl}/${account.id}`;
    return this.http.put<Account>(url, account, httpOptions);
  }

  private log(message: string) {
    this.messagesService.add('CategoryService: ' + message);
  }

  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {

      // TODO: send the error to remote logging infrastructure
      console.error(error); // log to console instead

      // TODO: better job of transforming error for user consumption
      this.log(`${operation} failed: ${error.message}`);

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }
}
