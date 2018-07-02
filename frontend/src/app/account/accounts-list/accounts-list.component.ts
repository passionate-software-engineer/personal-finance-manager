import { Component, OnInit } from '@angular/core';
import { Account } from '../account';
import { AccountService } from '../account-service/account.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-accounts-list',
  templateUrl: './accounts-list.component.html',
  styleUrls: ['./accounts-list.component.css']
})
export class AccountsListComponent implements OnInit {
  accounts: Account[];
  accountToAdd: Account;
  addAccount = true;
  addingMode = true;
  editingAccount = false;
  selectedAccount: Account = new Account();
  id;


  constructor(private accountService: AccountService) { }

  ngOnInit() {
    this.getAccounts();
  }


  getAccounts(): void {
    this.accountService.getAccounts()
      .subscribe(accounts => this.accounts = accounts);
  }

  deleteAccount(id: number) {
    this.accountService.deleteAccount(id).subscribe();
  }

  editAccount(account: Account) {
    this.editingAccount = true;
    this.selectedAccount = account;
  }

  onAddAccount(nameInput: HTMLInputElement, balanceInput: HTMLInputElement) {
    this.accountToAdd = new Account();
    this.accountToAdd.name = nameInput.value;
    this.accountToAdd.balance = +balanceInput.value;
    this.accountService.addAccount(this.accountToAdd)
      .subscribe(id => this.accountToAdd.id = id);
    this.accounts.push(this.accountToAdd);
  }

  onRefreshAccounts() {
    this.getAccounts();
  }

  sortByName(type: string) {
    if (type === 'normal') {
      this.accounts.sort((a1, a2) => (a1.name.toLowerCase() > a2.name.toLowerCase() ? -1 : 1));
    } else {
      this.accounts.sort((a1, a2) => (a1.name.toLowerCase() > a2.name.toLowerCase() ? 1 : -1));
    }
  }

  sortById(sortingType: string) {
    if (sortingType === 'normal') {
      this.accounts.sort((a1, a2) => a1.id - a2.id);
    } else {
      this.accounts.sort((a1, a2) => a2.id - a1.id);
    }
  }

  sortByBalance(sortingType: string) {
    if (sortingType === 'asc') {
      this.accounts.sort((a1, a2) => a1.balance - a2.balance);
    }
    if (sortingType === 'dsc') {
      this.accounts.sort((a1, a2) => a2.balance - a1.balance);
    }
  }
}
