import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs';
import {AuthenticationService} from '../authentication/authentication.service';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {

  constructor(private authenticationService: AuthenticationService) {
  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    // add authorization header with jwt token if available
    const currentUser = this.authenticationService.getLoggedInUser();
    if (currentUser && currentUser.token) {

      request = request.clone({
        setHeaders: {
          Authorization: `${currentUser.token}`
        }
      });

    }

    return next.handle(request);
  }
}
