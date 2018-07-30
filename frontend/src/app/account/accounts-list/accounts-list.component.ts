import {Component, OnInit} from '@angular/core';
import {Account} from '../account';
import {AccountService} from '../account-service/account.service';
import {isNumeric} from 'rxjs/internal-compatibility';
import {AlertsService} from '../../alerts/alerts-service/alerts.service';

const maxAccountBalance = Number.MAX_SAFE_INTEGER;
const minAccountBalance = Number.MIN_SAFE_INTEGER;

@Component({
  selector: 'app-accounts-list',
  templateUrl: './accounts-list.component.html',
  styleUrls: ['./accounts-list.component.css']
})

export class AccountsListComponent implements OnInit {
  accounts: Account[];
  addingMode = false;
  newAccountName: string;
  newAccountBalance: number;

  constructor(private accountService: AccountService, private alertService: AlertsService) {
  }

  ngOnInit() {
    this.getAccounts();
  }

  getAccounts(): void {
    this.accountService.getAccounts()
      .subscribe(accounts => {
        this.accounts = accounts;
        this.sortByName('asc');
      });
  }

  deleteAccount(account) {
    if (confirm('Are you sure You want to delete this account ?')) {
      this.accountService.deleteAccount(account.id)
        .subscribe(() => {
          this.alertService.success('Account deleted');
          const index: number = this.accounts.indexOf(account);
          if (index !== -1) {
            this.accounts.splice(index, 1);
          }
        });
    }
  }

  onShowEditMode(account: Account) {
    account.editMode = true;
    account.editedName = account.name;
    account.editedBalance = account.balance;
  }

  onEditAccount(account: Account) {
    if (!this.validateAccount(account.editedName, account.editedBalance)) {
      return;
    }
    const editedAccount: Account = new Account();
    editedAccount.id = account.id;
    editedAccount.name = account.editedName;
    editedAccount.balance = account.editedBalance;
    this.accountService.editAccount(editedAccount)
      .subscribe(() => {
        this.alertService.success('Account updated');
        Object.assign(account, editedAccount);
        this.sortByName('asc');
      });
  }

  onAddAccount() {
    const accountToAdd = new Account();
    if (!this.validateAddingAccount(this.newAccountName, this.newAccountBalance)) {
      return;
    }
    accountToAdd.name = this.newAccountName;
    // @ts-ignore
    accountToAdd.balance = this.newAccountBalance;
    this.accountService.addAccount(accountToAdd)
      .subscribe(id => {
        this.alertService.success('Account added');
        accountToAdd.id = id;
        this.accounts.push(accountToAdd);
        this.addingMode = false;
        this.newAccountBalance = null;
        this.newAccountName = null;
        this.sortByName('asc');
      });
  }

  onRefreshAccounts() {
    this.getAccounts();
  }

  sortByName(type: string) {
    if (type === 'asc') {
      this.accounts.sort((a1, a2) => (a1.name.toLowerCase() > a2.name.toLowerCase() ? 1 : -1));
    }
    if (type === 'dsc') {
      this.accounts.sort((a1, a2) => (a1.name.toLowerCase() > a2.name.toLowerCase() ? -1 : 1));
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

  validateAccount(accountName: string, accountBalance: number): boolean {
    if ((accountName == null || accountName.trim() === '')
      && (!accountBalance)) {
      this.alertService.error('Name cannot be empty');
      this.alertService.error('Balance cannot be empty');
      return false;
    }
    if (accountName == null || accountName === '') {
      this.alertService.error('Name cannot be empty');
      return false;
    }
    if (accountName.length > 70) {
      this.alertService.error('Account name too long. Account name can not be longer then 100 characters');
      return false;
    }

    if (typeof accountBalance === 'undefined' || !accountBalance) {
      this.alertService.error('Balance cannot be empty');
      return false;
    }

    if (!isNumeric(accountBalance)) {
      this.alertService.error('Provided balance is not correct number');
      return false;
    }

    const newAccountBalance = Math.round(accountBalance * 100) / 100;
    if (newAccountBalance > maxAccountBalance) {
      this.alertService.error('Balance number is too big.' +
        ' If You are so rich why do You need personal finance manager !? ');
      return false;
    }
    if (newAccountBalance < minAccountBalance) {
      this.alertService.error('Balance number is too low');
      return false;
    }
    return true;
  }

  validateAddingAccount(accountName: string, accountBalance: number): boolean {
    if (!this.validateAccount(accountName, accountBalance)) {
      return false;
    }

    if (this.accounts.filter(account => account.name.toLocaleLowerCase()
      === accountName.toLocaleLowerCase()).length > 0) {
      this.alertService.error('Account with provided name already exist');
      return false;
    }
    return true;
  }
}
