import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {AlertsService} from '../components/alert/alerts-service/alerts.service';
import {environment} from '../../environments/environment';
import {v4 as uuid} from 'uuid';
import {TranslateService} from '@ngx-translate/core';

export abstract class ServiceBase {

  // TODO move to interceptor
  protected httpCorrelationId = {
    headers: new HttpHeaders({'correlationId': uuid()})
  };

  // TODO move to interceptor
  protected httpOptions = { // TODO Correlation-Id to keep format (correct in backend too)
    headers: new HttpHeaders({'Content-Type': 'application/json', 'correlationId': uuid()})
  };

  constructor(protected http: HttpClient, protected alertService: AlertsService) {
  }

  protected static apiUrl(service: string, id: number = null): string {
    const idInUrl = (id !== null ? '/' + id : '');

    return environment.apiUrl + '/' + service + idInUrl;
  }

  // TODO move to error interceptor
  protected handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {

      this.alertService.error(error);

      return throwError(error);
    };
  }
}
