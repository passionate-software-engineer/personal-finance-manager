import { Injectable } from '@angular/core';
import { Account } from '../account';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};
@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private apiUrl = 'http://localhost:8081/accounts';

  constructor(private http: HttpClient) { }

  getAccounts(): Observable<Account[]> {
    return this.http.get<Account[]>(this.apiUrl);
  }

  // check this in case of work. Why do We need to return sth?
  addAccount(account: Account): Observable<Account> {
    return this.http.post<Account>(this.apiUrl, account, httpOptions);
  }

  deleteAccount(id: number): Observable<Account> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.delete<Account>(url);
  }
}
