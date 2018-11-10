import {Injectable} from '@angular/core';
import {interval, Subscription} from 'rxjs';
import {AlertsService} from '../components/alert/alerts-service/alerts.service';
import {Router} from '@angular/router';
import {AuthenticationService} from './authentication.service';
import {TranslateService} from '@ngx-translate/core';
import {HealthService} from './health.service';

@Injectable({
  providedIn: 'root'
})
export class HealthCheckTask {

  private healthCheckTask: Subscription;

  constructor(
    private router: Router,
    private authenticationService: AuthenticationService,
    private healthService: HealthService,
    private translate: TranslateService,
    private alertService: AlertsService) {

    authenticationService.currentUserObservable.subscribe(user => {
      if (user.token != null) {
        this.startHealthCheckTask();
      } else {
        this.stopHealthCheckTask();
      }
    });
  }

  private startHealthCheckTask(): void {
    this.healthCheckTask = interval(15 * 1000).subscribe(eventNumber => {
      // no need to do anything - error handler will do the job
      this.healthService.getHealthStatus().subscribe();

      const tokenExpirationTime = this.authenticationService.getLoggedInUser().tokenExpirationTime;
      if (tokenExpirationTime != null) {
        if (new Date(tokenExpirationTime) < new Date()) {
          this.router.navigate(['/login'], {queryParams: {returnUrl: this.router.url}});
          this.authenticationService.logout();
          this.alertService.error(this.translate.instant('message.loggedOut'));
        }
      }
    });
  }

  private stopHealthCheckTask(): void {
    this.healthCheckTask.unsubscribe();
  }
}
