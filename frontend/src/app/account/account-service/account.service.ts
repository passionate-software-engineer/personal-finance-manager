import {Injectable} from '@angular/core';
import {Account} from '../account';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {AlertsService} from '../../alerts/alerts-service/alerts.service';
import {ServiceBase} from '../../services/service-base';

const PATH = 'accounts';

@Injectable({
  providedIn: 'root'
})
export class AccountService extends ServiceBase {

  constructor(http: HttpClient, alertService: AlertsService) {
    super(http, alertService);
  }

  getAccounts(): Observable<Account[]> {
    return this.http.get<Account[]>(ServiceBase.apiUrl(PATH))
      .pipe(catchError(this.handleError('getAccounts', [])));
  }

  addAccount(account: Account): Observable<any> {
    return this.http.post<any>(ServiceBase.apiUrl(PATH), account, this.httpOptions)
      .pipe(catchError(this.handleError('addAccount', [])));
  }

  deleteAccount(id: number): Observable<any> {
    return this.http.delete<Account>(ServiceBase.apiUrl(PATH, id))
      .pipe(catchError(this.handleError('deleteAccount', [])));
  }

  editAccount(account: Account): Observable<any> {
    return this.http.put<Account>(ServiceBase.apiUrl(PATH, account.id), account, this.httpOptions)
      .pipe(catchError(this.handleError('editAccount', [])));
  }

}
