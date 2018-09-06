import {Component, OnInit} from '@angular/core';
import {Account} from '../account';
import {AccountService} from '../account-service/account.service';
import {isNumeric} from 'rxjs/internal-compatibility';
import {AlertsService} from '../../alert/alerts-service/alerts.service';
import {Sortable} from '../../../helpers/sortable';

const maxAccountBalance = Number.MAX_SAFE_INTEGER;
const minAccountBalance = Number.MIN_SAFE_INTEGER;

@Component({
  selector: 'app-accounts-list',
  templateUrl: './accounts.component.html',
  styleUrls: ['./accounts.component.css']
})

export class AccountsComponent extends Sortable implements OnInit {
  accounts: Account[] = [];
  addingMode = false;
  newAccount: Account = new Account();

  constructor(private accountService: AccountService, private alertService: AlertsService) {
    super('name');
  }

  ngOnInit() {
    this.getAccounts();
  }

  getAccounts(): void {
    this.accountService.getAccounts()
      .subscribe(accounts => {
        this.accounts = accounts;
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
    account.editedAccount = new Account();
    account.editedAccount.name = account.name;
    account.editedAccount.balance = account.balance;
  }

  onEditAccount(account: Account) {
    if (!this.validateAccount(account.editedAccount)) {
      return;
    }
    const editedAccount: Account = new Account();
    editedAccount.id = account.id;
    editedAccount.name = account.editedAccount.name;
    editedAccount.balance = account.editedAccount.balance;
    this.accountService.editAccount(editedAccount)
      .subscribe(() => {
        this.alertService.success('Account updated');
        Object.assign(account, editedAccount);
        // TODO - get object from server
      });
  }

  onAddAccount() {
    if (!this.validateAddingAccount(this.newAccount)) {
      return;
    }

    this.accountService.addAccount(this.newAccount)
      .subscribe(id => {
        this.alertService.success('Account added');
        this.newAccount.id = id;

        // TODO - get object from server
        this.accounts.push(this.newAccount);
        this.addingMode = false;
        this.newAccount = new Account();
      });
  }

  onRefreshAccounts() {
    this.getAccounts();
  }

  validateAccount(account: Account): boolean {
    if ((account.name == null || account.name.trim() === '')
      && (!account.balance)) { // TODO change validation to validate all at once, not break on error
      this.alertService.error('Name cannot be empty');
      this.alertService.error('Balance cannot be empty');
      return false;
    }
    if (account.name == null || account.name === '') {
      this.alertService.error('Name cannot be empty');
      return false;
    }
    if (account.name.length > 100) {
      this.alertService.error('Account name too long. Account name can not be longer then 100 characters');
      return false;
    }

    if (account.balance == null) {
      this.alertService.error('Balance cannot be empty');
      return false;
    }

    if (!isNumeric(account.balance)) {
      this.alertService.error('Provided balance is not a correct number');
      return false;
    }

    if (account.balance > maxAccountBalance) {
      this.alertService.error('Balance number is too big.' +
        ' If You are so rich why do You need personal finance manager !? ');
      return false;
    }

    if (account.balance < minAccountBalance) {
      this.alertService.error('Balance number is too low');
      return false;
    }

    return true;
  }

  validateAddingAccount(accountToValidate: Account): boolean {
    if (!this.validateAccount(accountToValidate)) {
      return false;
    }

    if (this.accounts.filter(account => account.name.toLocaleLowerCase() === accountToValidate.name.toLocaleLowerCase()).length > 0) {
      this.alertService.error('Account with provided name already exist');
      return false;
    }
    return true;
  }
}
