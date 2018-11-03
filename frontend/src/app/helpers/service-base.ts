import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {AlertsService} from '../components/alert/alerts-service/alerts.service';
import {environment} from '../../environments/environment';

export abstract class ServiceBase {

  protected contentType = {
    headers: new HttpHeaders({'Content-Type': 'application/json'})
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
