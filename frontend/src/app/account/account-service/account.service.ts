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
  private apiUrl = 'http://localhost:8080/v1/accounts/';
  // accounts: Account[] = [
  //   { id: 1, desc: 'ing', balance: 1234 },
  //   { id: 2, desc: 'mbank', balance: 19994 },
  //   { id: 3, desc: 'idea', balance: 765 },
  //   { id: 4, desc: 'millenium', balance: 987654 }
  // ];
  constructor(private http: HttpClient) { }

  // getAccounts(): Account[] {
  //   return this.accounts;
  // }

  getCompanies(): Observable<Account[]> {
    return this.http.get<Account[]>(this.apiUrl);
  }
}
