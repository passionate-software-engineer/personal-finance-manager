import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {AuthenticationService} from '../authentication/authentication.service';
import {v4 as uuid} from 'uuid';
import {Observable} from 'rxjs';

@Injectable()
export class HeadersInterceptor implements HttpInterceptor {

  constructor(private authenticationService: AuthenticationService) {
  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    // add authorization header with jwt token if available
    const currentUser = this.authenticationService.getLoggedInUser();
    if (currentUser && currentUser.token) {

      request = request.clone({
        setHeaders: {
          Authorization: `${currentUser.token}`,
          'Correlation-Id': uuid(),
          'Language': localStorage.getItem('language')
        }
      });

    } else {
      request = request.clone({
        setHeaders: {
          'Correlation-Id': uuid(),
          'Language': localStorage.getItem('language')
        }
      });
    }

    return next.handle(request);
  }
}
