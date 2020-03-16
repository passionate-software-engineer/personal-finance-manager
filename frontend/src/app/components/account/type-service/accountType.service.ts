import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ServiceBase} from '../../../helpers/service-base';
import {AccountType} from '../accountType';

const PATH = 'accountType';

@Injectable({
  providedIn: 'root'
})
export class AccountTypeService extends ServiceBase {

  constructor(http: HttpClient) {
    super(http);
  }
 private static accountTypeToAccountTypeRequest(accountType: AccountType) {
    return {
      name: accountType.name
    };
  }

  getAccountTypes(): Observable<AccountType[]> {
    return this.http.get<AccountType[]>(ServiceBase.apiUrl(PATH));
  }

  deleteAccountType(id: number): Observable<any> {
    return this.http.delete<AccountType>(ServiceBase.apiUrl(PATH, id));
  }

  editAccountType(accountType: AccountType): Observable<any> {
     return this.http.put<AccountType>(ServiceBase.apiUrl(PATH, accountType.id), AccountTypeService.accountTypeToAccountTypeRequest(accountType), this.contentType);
 }

}
