import { Component, OnInit } from '@angular/core';
import { Account } from '../account';
import { AccountService } from '../account-service/account.service';

@Component({
  selector: 'app-accounts-list',
  templateUrl: './accounts-list.component.html',
  styleUrls: ['./accounts-list.component.css']
})
export class AccountsListComponent implements OnInit {
  accounts: Account[];
  addAccount = true;

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

}
