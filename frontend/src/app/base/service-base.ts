import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {AlertsService} from '../alerts/alerts-service/alerts.service';
import {environment} from '../../environments/environment';


export abstract class ServiceBase {

  protected httpOptions = {
    headers: new HttpHeaders({'Content-Type': 'application/json'})
    // TODO send correlation id in each request
  };

  constructor(protected http: HttpClient, protected alertService: AlertsService) {
  }

  protected static apiUrl(service: string, id: number = null): string {
    const idInUrl = (id !== null ? '/' + id : '');

    return environment.appUrl + '/' + service + idInUrl;
  }

  protected handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      if (error.status === 0) {
        this.alertService.error('No connectivity with backend service, please try again later');
      }

      if (error.status === 400) {
        this.alertService.error(error.error);
      }

      if (error.status === 500) {
        this.alertService.error('Something went wrong, please try again later');
      }

      return throwError(error);
    };
  }
}
