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
  private isSessionExtensionInProgress = false;
  private number = 0;
  private healthCheckTaskCounter = 0;

  constructor(
    private router: Router,
    private authenticationService: AuthenticationService,
    private ngZone: NgZone,
    private healthService: HealthService,
    private translate: TranslateService,
    private alertService: AlertsService,
    private userService: UserService) {

    authenticationService.currentUserObservable.subscribe(user => {
      if (user.accessToken != null && this.healthCheckTask == null) {
        this.startHealthCheckTask();
      } else if (user.accessToken == null) {
        this.stopHealthCheckTask();
      }
    });
  }

  private startHealthCheckTask(): void {

    this.ngZone.runOutsideAngular(() => { // needed for interval to work with protractor https://github.com/angular/protractor/issues/3349

      this.healthCheckTask = interval(10 * 1000)
      .subscribe(eventNumber => {
        console.log('health-check-task @ interval', ++this.healthCheckTaskCounter),

          this.ngZone.run(() => {
            console.log('    ng-Zone.run'),

              // no need to do anything - error handler will do the job
              this.healthService.getHealthStatus()
                  .subscribe();

            const tokenExpirationTime = this.authenticationService.getLoggedInUser().accessTokenExpirationTime;
            const refreshTokenExpirationTime = this.authenticationService.getLoggedInUser().refreshTokenExpirationTime;
            const refreshTokenExpirationTimeInSeconds = Math.floor((new Date(refreshTokenExpirationTime).getTime() - Date.now()) / 1000);

            if (this.authenticationService.getLoggedInUser() && refreshTokenExpirationTimeInSeconds > 10) {
              console.log('       refreshToken expiration > 10');
              console.log('');

              if (tokenExpirationTime != null) {

                const expireTimeInSeconds = Math.floor((new Date(tokenExpirationTime).getTime() - Date.now()) / 1000);
                if (expireTimeInSeconds < 30) {
                  this.isSessionExtensionInProgress = false;

                  // this.promptForPasswordAndTryToExtendSession(expireTimeInSeconds);
                  const currentUser = JSON.parse(sessionStorage.getItem('currentUser'));
                  this.userService.extendToken(currentUser.refreshToken)
                      .subscribe(
                        newAccessToken => {
                          console.log('number: ', ++this.number),
                            console.log('received token: ', newAccessToken.token),
                            console.log('received token expiration time: ', newAccessToken.tokenExpiryDate),
                            console.log('refresh token: ', currentUser.refreshToken),
                            console.log('refresh token expires at : ', currentUser.refreshTokenExpirationTime),
                            console.log(' '),
                            currentUser.accessToken = newAccessToken.token;
                          currentUser.accessTokenExpirationTime = newAccessToken.tokenExpiryDate;

                          sessionStorage.setItem('currentUser', JSON.stringify(currentUser));

                        },
                        err => console.log('error = ', err.toString()),
                      );
                }

                if (new Date(tokenExpirationTime) < new Date()) {
                  this.router.navigate(['/login'], {queryParams: {returnUrl: this.router.url}});
                  this.authenticationService.logout();
                  this.alertService.error(this.translate.instant('message.loggedOut'));
                }
              }
            } else {
              if (this.authenticationService.isUserLoggedIn() && !this.isSessionExtensionInProgress) {
                this.isSessionExtensionInProgress = true;
                console.log('window: ', ++this.number),

                  this.promptForPasswordAndTryToExtendSession();
              }
            }
          });

      });
    });

  }

  private promptForPasswordAndTryToExtendSession(expireTimeInSeconds?) {
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
              alert('Provided credentials were invalid. Logging out');
              this.authenticationService.logout();
            });
    }
  }

  private stopHealthCheckTask(): void {
    this.healthCheckTask.unsubscribe();
    this.healthCheckTask = null;
  }
}
