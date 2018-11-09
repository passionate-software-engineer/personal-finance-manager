import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ServiceBase} from '../helpers/service-base';
import {AlertsService} from '../components/alert/alerts-service/alerts.service';

const PATH = 'actuator/health';

@Injectable({
  providedIn: 'root'
})
export class HealthService extends ServiceBase {

  constructor(http: HttpClient, alertService: AlertsService) {
    super(http, alertService);
  }

  getHealthStatus(): Observable<string> {
    return this.http.get<string>(ServiceBase.apiUrl(PATH));
  }
}
