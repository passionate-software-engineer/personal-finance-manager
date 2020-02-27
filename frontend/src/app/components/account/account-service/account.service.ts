import {Injectable} from '@angular/core';
import {Account} from '../account';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ServiceBase} from '../../../helpers/service-base';

const PATH = 'accounts';

@Injectable({
  providedIn: 'root'
})
export class AccountService extends ServiceBase {

  constructor(http: HttpClient) {
    super(http);
  }

  private static accountToAccountRequest(account: Account) {
    return {
      name: account.name,
      accountTypeId: account.type.id,
      balance: account.balance,
      currencyId: account.currency.id
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

  markAccountAsVerifiedToday(account: Account): Observable<any> {
    return this.http.patch<any>(ServiceBase.apiUrl(PATH + '/' + account.id + '/markAccountAsVerifiedToday'), '', this.contentType);
  }

  restoreAccount(account: Account): Observable<any> {
    return this.setAccountStatus(account, true);
  }

  archiveAccount(account: Account): Observable<any> {
    return this.setAccountStatus(account, false);
  }

  private setAccountStatus(account: Account, toBeActive: boolean): Observable<any> {
    const pathEnd = toBeActive ? '/markAsActive' : '/markAsArchived';
    return this.http.patch<any>(ServiceBase.apiUrl(PATH + '/' + account.id + pathEnd), '', this.contentType);
  }

}
