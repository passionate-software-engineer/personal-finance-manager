import {Injectable} from '@angular/core';
import {History} from '../history';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {AlertsService} from '../../alert/alerts-service/alerts.service';
import {ServiceBase} from '../../../helpers/service-base';

const PATH = 'history';

@Injectable({
  providedIn: 'root'
})
export class HistoryService extends ServiceBase {

  constructor(http: HttpClient, alertService: AlertsService) {
    super(http, alertService);
  }

  getHistory(): Observable<History[]> {
    return this.http.get<History[]>(ServiceBase.apiUrl(PATH));
  }
}
