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
  private number = 0;
  private healthCheckTaskCounter = 0;
  private isPromptAlreadyShowed = false;

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

      this.healthCheckTask = interval(30 * 1000)
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

            if (this.authenticationService.getLoggedInUser() && refreshTokenExpirationTimeInSeconds > 15) {

              this.isPromptAlreadyShowed = false;

              console.log('       refreshToken expiration > 10');
              console.log('');

              if (tokenExpirationTime != null) {

                const expireTimeInSeconds = Math.floor((new Date(tokenExpirationTime).getTime() - Date.now()) / 1000);
                if (expireTimeInSeconds < 30) {

                  const currentUser = JSON.parse(localStorage.getItem('currentUser'));
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

                          localStorage.setItem('currentUser', JSON.stringify(currentUser));

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
              if (this.authenticationService.isUserLoggedIn() && !this.isPromptAlreadyShowed) {
                console.log('window: ', ++this.number),
                  this.isPromptAlreadyShowed = true;
                this.promptForPasswordAndTryToExtendSession();
              }
            }
          });

      });
    });

  }

  private promptForPasswordAndTryToExtendSession() {
    const password = prompt('Your session has expired, please enter a password to extend it.', '');
    if (password != null) {
      const username = this.authenticationService.getLoggedInUser().username;
      this.authenticationService.login(username, password)
          .subscribe(
            data => {
              const refreshTokenExpirationTime = this.authenticationService.getLoggedInUser().refreshTokenExpirationTime;
              if (refreshTokenExpirationTime != null) {
                const refreshTokenExpireTimeInMinutes = Math.round((new Date(refreshTokenExpirationTime).getTime() - Date.now()) / 1000 / 60);
                alert('Your session was extended for next ' + refreshTokenExpireTimeInMinutes + ' minutes, thank you.');
              }
            },
            error => {
              this.terminateSessionAndNavigateToLoginPage();
            });
    } else {
     this.terminateSessionAndNavigateToLoginPage();

    }
  }

  private terminateSessionAndNavigateToLoginPage() {
    this.router.navigate(['/login'], {queryParams: {returnUrl: this.router.url}});
    this.authenticationService.logout();
    this.alertService.error(this.translate.instant('message.loggedOut'));
  }

  private stopHealthCheckTask(): void {
    this.healthCheckTask.unsubscribe();
    this.healthCheckTask = null;
  }
}
