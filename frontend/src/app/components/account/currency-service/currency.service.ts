import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ServiceBase} from '../../../helpers/service-base';
import {Currency} from '../currency';

const PATH = 'currencies';

@Injectable({
  providedIn: 'root'
})
export class CurrencyService extends ServiceBase {

  constructor(http: HttpClient) {
    super(http);
  }

  getCurrencies(): Observable<Currency[]> {
    return this.http.get<Currency[]>(ServiceBase.apiUrl(PATH));
  }

}
