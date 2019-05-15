import {Injectable, NgZone} from '@angular/core';
import {interval, Subscription} from 'rxjs';
import {AlertsService} from '../components/alert/alerts-service/alerts.service';
import {Router} from '@angular/router';
import {AuthenticationService} from './authentication.service';
import {TranslateService} from '@ngx-translate/core';
import {HealthService} from './health.service';
import {UserService} from './user.service';

@Injectable({
  providedIn: 'root'
})
export class HealthCheckTask {

  private healthCheckTask: Subscription;

  constructor(
    private router: Router,
    private authenticationService: AuthenticationService,
    private ngZone: NgZone,
    private healthService: HealthService,
    private translate: TranslateService,
    private alertService: AlertsService,
    private userService: UserService) {

    authenticationService.currentUserObservable.subscribe(user => {
      if (user.accessToken != null) {
        this.startHealthCheckTask();
      } else {
        this.stopHealthCheckTask();
      }
    });
  }

  private startHealthCheckTask(): void {
    this.ngZone.runOutsideAngular(() => { // needed for interval to work with protractor https://github.com/angular/protractor/issues/3349

      this.healthCheckTask = interval(30 * 1000)
      .subscribe(eventNumber => {

        this.ngZone.run(() => {
          // no need to do anything - error handler will do the job
          this.healthService.getHealthStatus()
              .subscribe();

          this.userService.extendToken('hello from frontend')
          .subscribe();


          const tokenExpirationTime = this.authenticationService.getLoggedInUser().accessTokenExpirationTime;
          if (tokenExpirationTime != null) {

            const expireTimeInSeconds = Math.floor((new Date(tokenExpirationTime).getTime() - Date.now()) / 1000);
            if (expireTimeInSeconds < 120) {
              /**
               * send request to extend session if access accessToken is about to expire instead of popping out window
               */
              this.promptForPasswordAndTryToExtendSession(expireTimeInSeconds);
            }

            if (new Date(tokenExpirationTime) < new Date()) {
              this.router.navigate(['/login'], {queryParams: {returnUrl: this.router.url}});
              this.authenticationService.logout();
              this.alertService.error(this.translate.instant('message.loggedOut'));
            }
          }
        });

      });
    });

  }

  /**
   *
   * instead this method write new one for sending refresh accessToken to /users/refresh
   */
  private promptForPasswordAndTryToExtendSession(expireTimeInSeconds) {
    const password = prompt('Your session will expire in ' + expireTimeInSeconds
      + ' seconds, please enter a password to extend it.', '');
    if (password != null) {
      const username = this.authenticationService.getLoggedInUser().username;
      this.authenticationService.login(username, password)
          .subscribe(
            data => {
              const tokenExpirationTime = this.authenticationService.getLoggedInUser().accessTokenExpirationTime;
              if (tokenExpirationTime != null) {
                const expireTimeInMinutes = Math.round((new Date(tokenExpirationTime).getTime() - Date.now()) / 1000 / 60);
                alert('Your session was extended for next ' + expireTimeInMinutes + ' minutes, thank you.');
              }
            },
            error => {
              alert('Provided credentials were invalid, please try again on next prompt.');
            });
    }
  }

  private stopHealthCheckTask(): void {
    this.healthCheckTask.unsubscribe();
  }
}
