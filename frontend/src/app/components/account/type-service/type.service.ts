import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ServiceBase} from '../../../helpers/service-base';
import {AccountType} from '../type';

const PATH = 'accountType';

@Injectable({
  providedIn: 'root'
})
export class AccountTypeService extends ServiceBase {

  constructor(http: HttpClient) {
    super(http);
  }

  getAccountType(): Observable<AccountType[]> {
    return this.http.get<AccountType[]>(ServiceBase.apiUrl(PATH));
  }

}
