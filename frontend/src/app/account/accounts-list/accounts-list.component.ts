import {Component, OnInit} from '@angular/core';
import {Account} from '../account';
import {AccountService} from '../account-service/account.service';
import {isNumeric} from 'rxjs/internal-compatibility';
import {AlertsService} from '../../alerts/alerts-service/alerts.service';

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
        if (accounts === null) {
          this.accounts = [];
        } else {
          this.accounts = accounts;
        }
      });
  }

  deleteAccount(account) {
    this.accountService.deleteAccount(account.id).subscribe(() => {
      this.alertService.info('Account deleted');
    });
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
    if (!this.validateAccount(this.editedName, this.editedBalance)) {
      return;
    }
    account.name = this.editedName;
    account.balance = this.editedBalance;
    this.accountService.editAccount(account).subscribe(
      () => {
        this.alertService.info('Account updated');
      }
    );
    account.editMode = false;
  }

  onAddAccount() {
    this.accountToAdd = new Account();
    if (!this.validateAccount(this.newAccountName, this.newAccountBalance)) {
      return;
    }
    this.accountToAdd.name = this.newAccountName;
    // @ts-ignore
    this.accountToAdd.balance = (parseFloat(this.newAccountBalance)).toFixed(2);
    this.accountService.addAccount(this.accountToAdd)
      .subscribe(id => {
        if (isNumeric(id)) {
          this.alertService.success('Account added');
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

  validateAccount(accountName: string, accountBalance: number): boolean {
    if ((accountName == null || accountName === '')
      && (!accountBalance)) {
      this.alertService.error('Name cannot be empty');
      this.alertService.error('Balance cannot be empty');
      return false;
    }
    if (accountName == null || accountName === '') {
      this.alertService.error('Name cannot be empty');
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

    if ((Math.round(accountBalance * 100) / 100) > 999999999999999) {
      this.alertService.error('Balance number is too big.' +
        ' If You are so rich why do You need personal finance manager !? ');
      return false;
    }
    return true;
  }
}
