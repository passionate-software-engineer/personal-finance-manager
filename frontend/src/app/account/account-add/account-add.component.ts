import { Component, OnInit } from '@angular/core';
import { AccountService } from '../account-service/account.service';
import { Account } from '../account';

@Component({
  selector: 'app-account-add',
  templateUrl: './account-add.component.html',
  styleUrls: ['./account-add.component.css']
})
export class AccountAddComponent implements OnInit {
  accountToAdd: Account = new Account();
  constructor(private accountService: AccountService) { }

  ngOnInit() {
  }

  onAddAccount(account: Account): void {
    this.accountService.addAccount(account).subscribe();
  }
}
