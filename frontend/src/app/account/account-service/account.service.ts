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
  private apiUrl = 'http://localhost:8081/accounts/';

  constructor(private http: HttpClient) { }

  getCompanies(): Observable<Account[]> {
    return this.http.get<Account[]>(this.apiUrl);
  }
}
