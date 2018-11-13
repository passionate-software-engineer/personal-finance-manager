import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {AuthenticationService} from '../authentication/authentication.service';
import {TranslateService} from '@ngx-translate/core';
import {Router} from '@angular/router';
import {AlertsService} from '../components/alert/alerts-service/alerts.service';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {

  constructor(
    private authenticationService: AuthenticationService,
    private translate: TranslateService,
    private router: Router,
    private alertService: AlertsService
  ) {
  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    return next.handle(request).pipe(catchError(err => {

      // auto logout if 401 response returned from api
      if (err.status === 401) {
        if (this.authenticationService.isUserLoggedIn()) {
          this.authenticationService.logout();
        }
        this.router.navigate(['/login'], {queryParams: {returnUrl: this.router.url}});
        this.alertService.error(this.translate.instant('message.loggedOut'));
        return throwError('');
      }

      if (err.status === 0) {
        if (this.authenticationService.isUserLoggedIn()) {
          this.authenticationService.logout();
        }
        this.router.navigate(['/login'], {queryParams: {returnUrl: this.router.url}});
        this.alertService.error(this.translate.instant('message.noConnectivity'));
        return throwError('');
      }

      if (err.status === 500) {
        this.alertService.error(this.translate.instant('message.somethingWentWrong'));
        return throwError('');
      }

      this.alertService.error(err.error || this.translate.instant('message.internalSystemError'));

      return throwError('');
    }));
  }
}
