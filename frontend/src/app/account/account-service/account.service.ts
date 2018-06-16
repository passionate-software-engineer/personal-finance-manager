import { Injectable } from '@angular/core';
import { Account } from '../account';

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  accounts: Account[] = [
    { id: 1, desc: 'ing', balance: 1234 },
    { id: 2, desc: 'mbank', balance: 19994 },
    { id: 3, desc: 'idea', balance: 765 },
    { id: 4, desc: 'millenium', balance: 987654 }
  ];
  constructor() { }

  getAccounts(): Account[] {
    return this.accounts;
  }
}
