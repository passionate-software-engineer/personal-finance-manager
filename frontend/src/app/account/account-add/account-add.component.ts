import { Component, OnInit, Input, OnChanges } from '@angular/core';
import { AccountService } from '../account-service/account.service';
import { Account } from '../account';

@Component({
  selector: 'app-account-add',
  templateUrl: './account-add.component.html',
  styleUrls: ['./account-add.component.css']
})
export class AccountAddComponent implements OnInit, OnChanges {
  @Input() account: Account = new Account();
  @Input() editAccount = false;
  name = this.account.name;
  balance = this.account.balance;

  constructor(private accountService: AccountService) { }

  ngOnInit() {
  }

  ngOnChanges() {
    this.name = this.account.name;
    this.balance = this.account.balance;
  }

  onAddAccount(account: Account): void {
    this.account.name = this.name;
    this.account.balance = this.balance;
    this.accountService.addAccount(account).subscribe();
  }

  onEditAccount(account: Account) {
    this.editAccount = false;
    this.account.name = this.name;
    this.account.balance = this.balance;
    this.accountService.editAccount(account).subscribe();
    this.account = new Account();
    this.name = '';
    this.balance = null;
  }

  onExit() {
    this.editAccount = false;
    this.account = new Account();
    this.name = '';
    this.balance = null;
  }

  onReset() {
    this.name = '';
    this.balance = null;
  }
}
