import {HttpClient, HttpHeaders} from '@angular/common/http';
import {environment} from '../../environments/environment';

export abstract class ServiceBase {

  protected contentType = {
    headers: new HttpHeaders({'Content-Type': 'application/json'})
  };

  protected constructor(protected http: HttpClient) {
  }

  protected static apiUrl(service: string, id: number = null): string {
    const idInUrl = (id !== null ? '/' + id : '');

    return environment.apiUrl + '/' + service + idInUrl;
  }


}
