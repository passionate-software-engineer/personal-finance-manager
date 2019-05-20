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

    function getTokenExpirationTimeInSeconds(refreshTokenExpirationTime) {
      return Math.floor((new Date(refreshTokenExpirationTime).getTime() - Date.now()) / 1000);
    }

    this.ngZone.runOutsideAngular(() => { // needed for interval to work with protractor https://github.com/angular/protractor/issues/3349

        this.healthCheckTask = interval(30 * 1000)
        .subscribe(eventNumber => {
            console.log('health-check-task @ interval', ++this.healthCheckTaskCounter),

              this.ngZone.run(() => {
                  console.log('    ng-Zone.run'),

                    // no need to do anything - error handler will do the job
                    this.healthService.getHealthStatus()
                        .subscribe();

                  const accessTokenExpirationTime = this.authenticationService.getLoggedInUser().accessTokenExpirationTime;
                  const accessTokenExpirationTimeInSeconds = getTokenExpirationTimeInSeconds(accessTokenExpirationTime);

                  const refreshTokenExpirationTime = this.authenticationService.getLoggedInUser().refreshTokenExpirationTime;
                  const refreshTokenExpirationTimeInSeconds = getTokenExpirationTimeInSeconds(refreshTokenExpirationTime);

                  if (this.authenticationService.getLoggedInUser()) {
                    if (refreshTokenExpirationTimeInSeconds < 60) {
                      this.promptForPasswordAndTryToExtendSession();

                    } else if (accessTokenExpirationTimeInSeconds < 60) {
                      const currentUser = JSON.parse(localStorage.getItem('currentUser'));
                      this.userService.extendToken(currentUser.refreshToken)
                          .subscribe(
                            newAccessToken => {
                              currentUser.accessToken = newAccessToken.token;
                              currentUser.accessTokenExpirationTime = newAccessToken.tokenExpiryDate;

                              localStorage.setItem('currentUser', JSON.stringify(currentUser));
                            },
                            err => console.log('error = ', err.toString()),
                          );
                    }
                  }
                }
              );
          }
        );
      }
    );
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

