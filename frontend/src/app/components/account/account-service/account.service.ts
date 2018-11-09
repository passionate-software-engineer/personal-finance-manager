import {Injectable} from '@angular/core';
import {Account} from '../account';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {AlertsService} from '../../alert/alerts-service/alerts.service';
import {ServiceBase} from '../../../helpers/service-base';

const PATH = 'accounts';

@Injectable({
  providedIn: 'root'
})
export class AccountService extends ServiceBase {

  constructor(http: HttpClient, alertService: AlertsService) {
    super(http, alertService);
  }

  private static accountToAccountRequest(account: Account) {
    return {
      name: account.name,
      balance: account.balance
    };
  }

  getAccounts(): Observable<Account[]> {
    return this.http.get<Account[]>(ServiceBase.apiUrl(PATH));
  }

  addAccount(account: Account): Observable<any> {
    return this.http.post<any>(ServiceBase.apiUrl(PATH), AccountService.accountToAccountRequest(account), this.contentType);
  }

  deleteAccount(id: number): Observable<any> {
    return this.http.delete<Account>(ServiceBase.apiUrl(PATH, id));
  }

  editAccount(account: Account): Observable<any> {
    return this.http.put<Account>(ServiceBase.apiUrl(PATH, account.id), AccountService.accountToAccountRequest(account), this.contentType);
  }

}
