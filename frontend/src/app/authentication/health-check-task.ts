import {Injectable, NgZone} from '@angular/core';
import {interval, Subscription} from 'rxjs';
import {AlertsService} from '../components/alert/alerts-service/alerts.service';
import {Router} from '@angular/router';
import {AuthenticationService} from './authentication.service';
import {TranslateService} from '@ngx-translate/core';
import {HealthService} from './health.service';
import {UserService} from './user.service';
import {environment} from '../../environments/environment';

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
      if (user.accessToken != null && this.healthCheckTask == null) {
        this.startHealthCheckTask();
      } else if (user.accessToken == null) {
        this.stopHealthCheckTask();
      }
    });
  }

  private startHealthCheckTask(): void {

    this.ngZone.runOutsideAngular(() => { // needed for interval to work with protractor https://github.com/angular/protractor/issues/3349

        this.healthCheckTask = interval(environment.healthCheckTaskIntervalInSeconds)
        .subscribe(eventNumber => {

            this.ngZone.run(() => {
              // no need to do anything - error handler will do the job
              this.healthService.getHealthStatus()
                  .subscribe();

              if (this.authenticationService.getLoggedInUser()) {
                const accessTokenExpirationTime = this.authenticationService.getLoggedInUser().accessToken.expiryDate;
                const refreshTokenExpirationTime = this.authenticationService.getLoggedInUser().refreshToken.expiryDate;

                if (this.isExpired(accessTokenExpirationTime) && this.isExpired(refreshTokenExpirationTime)) {
                  this.terminateSessionAndNavigateToLoginPage();
                }

                const accessTokenExpirationTimeInSeconds = this.getTokenExpirationTimeInSeconds(accessTokenExpirationTime);
                const refreshTokenExpirationTimeInSeconds = this.getTokenExpirationTimeInSeconds(refreshTokenExpirationTime);

                if (this.authenticationService.getLoggedInUser()) {
                  if (refreshTokenExpirationTimeInSeconds < 60) {
                    if (this.healthCheckTask) {
                      this.stopHealthCheckTask();
                    }
                    this.promptForPasswordAndTryToExtendSession();
                    if (this.healthCheckTask == null) {
                      this.startHealthCheckTask();
                    }

                  } else if (accessTokenExpirationTimeInSeconds < 60) {
                    const currentUser = JSON.parse(sessionStorage.getItem('currentUser'));
                    this.userService.extendToken(currentUser.refreshToken.value)
                        .subscribe(
                          newAccessToken => {
                            currentUser.accessToken.value = newAccessToken.value;
                            currentUser.accessToken.expiryDate = newAccessToken.expiryDate;

                            sessionStorage.setItem('currentUser', JSON.stringify(currentUser));
                          },
                        );
                  }
                }
              }
            });
          }
        );
      }
    );
  }

  private promptForPasswordAndTryToExtendSession() {
    const password = prompt(this.translate.instant('prompt.extend.session'), '');
    if (password != null) {
      const username = this.authenticationService.getLoggedInUser().username;
      this.authenticationService.login(username, password)
          .subscribe(
            data => {
              const refreshTokenExpirationTime = this.authenticationService.getLoggedInUser().refreshToken.expiryDate;
              if (refreshTokenExpirationTime != null) {
                const refreshTokenExpireTimeInMinutes = this.getTokenExpirationTimeInMinutes(refreshTokenExpirationTime);
                alert('Your session was extended for next ' + refreshTokenExpireTimeInMinutes + ' minutes, thank you.');
                alert(this.translate.instant('prompt.extend.session.info1') + refreshTokenExpireTimeInMinutes +
                  this.translate.instant('prompt.extend.session.info2'));
              }
            },
            error => {
              this.terminateSessionAndNavigateToLoginPage();
            });
    } else {
      this.terminateSessionAndNavigateToLoginPage();
    }
  }

  private getTokenExpirationTimeInMinutes(refreshTokenExpirationTime) {
    return Math.round(this.getTokenExpirationTimeInSeconds(refreshTokenExpirationTime) / 60);
  }

  private getTokenExpirationTimeInSeconds(refreshTokenExpirationTime) {
    return Math.floor((new Date(refreshTokenExpirationTime).getTime() - Date.now()) / 1000);
  }

  private isExpired(date: string): boolean {
    return new Date(date) < new Date();
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
