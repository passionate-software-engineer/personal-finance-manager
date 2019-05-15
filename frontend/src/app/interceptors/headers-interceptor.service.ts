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

    // add authorization header with jwt accessToken if available
    const currentUser = this.authenticationService.getLoggedInUser();
    /**
     * check presence of refresh accessToken? - possibly not as refresh will be required when we need to extend session
     *
     * if(currentuser && accessToken  - when we want to extend session
     * Authorization: `${currentUser.refreshToken}`,  ?
     */
    if (currentUser && currentUser.accessToken) {    // if(logged-in)

      request = request.clone({
        setHeaders: {
          /**
           * append refreshToken to header or replace refresh accessToken with Refresh accessToken, but not here I think, as refresh will be required when we
           * need to extend session
           */
          Authorization: `${currentUser.accessToken}`,
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
