import {Component, OnInit} from '@angular/core';
import {Account} from '../account';
import {AccountService} from '../account-service/account.service';

@Component({
  selector: 'app-accounts-list',
  templateUrl: './accounts-list.component.html',
  styleUrls: ['./accounts-list.component.css']
})
export class AccountsListComponent implements OnInit {
  accounts: Account[];
  accountToAdd: Account;
  addingMode = false;
  editedName: string;
  editedBalance: number;
  selectedAccount: Account = new Account();
  id;

  constructor(private accountService: AccountService) {
  }

  ngOnInit() {
    this.getAccounts();
  }

  getAccounts(): void {
    this.accountService.getAccounts()
      .subscribe(accounts => {
        if (accounts === null) {
          this.accounts = [];
        } else {
          this.accounts = accounts;
        }
      });
  }

  deleteAccount(account) {
    this.accountService.deleteAccount(account.id).subscribe();
    const index: number = this.accounts.indexOf(account);
    if (index !== -1) {
      this.accounts.splice(index, 1);
    }
  }

  onShowEditMode(account: Account) {
    account.editMode = true;
    this.editedBalance = account.balance;
    this.editedName = account.name;
  }

  onEditAccount(account: Account) {
    account.name = this.editedName;
    account.balance = this.editedBalance;
    this.accountService.editAccount(account).subscribe();
    account.editMode = false;
  }

  onAddAccount(nameInput: HTMLInputElement, balanceInput: HTMLInputElement) {
    this.accountToAdd = new Account();
    this.accountToAdd.id = null;
    this.accountToAdd.name = nameInput.value;
    if (balanceInput.value.length > 0 && isNaN(parseFloat(balanceInput.value))) {
      alert('Balance must be number');
      return;
    }
    if (balanceInput.value.length < 1) {
      this.accountToAdd.balance = null;
    } else {
      this.accountToAdd.balance = +balanceInput.value;
    }

    this.accountService.addAccount(this.accountToAdd)
      .subscribe(id => {
        if (id.isNumber) {
          this.accountToAdd.id = id;
          this.accounts.push(this.accountToAdd);
        }
      });
    this.addingMode = false;
  }

  onRefreshAccounts() {
    this.getAccounts();
  }

  sortByName(type: string) {
    if (type === 'asc') {
      this.accounts.sort((a1, a2) => (a1.name.toLowerCase() > a2.name.toLowerCase() ? -1 : 1));
    }
    if (type === 'dsc') {
      this.accounts.sort((a1, a2) => (a1.name.toLowerCase() > a2.name.toLowerCase() ? 1 : -1));
    }
  }

  sortById(sortingType: string) {
    if (sortingType === 'asc') {
      this.accounts.sort((a1, a2) => a1.id - a2.id);
    }
    if (sortingType === 'dsc') {
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
