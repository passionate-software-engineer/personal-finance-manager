import {Component, OnInit} from '@angular/core';
import {User} from '../../authentication/user';
import {UserService} from '../../authentication/user.service';
import {AuthenticationService} from '../../authentication/authentication.service';
import {TranslateService} from '@ngx-translate/core';
import {interval} from 'rxjs';
import {HealthService} from '../../authentication/health.service';
import {Router} from '@angular/router';
import {AlertsService} from '../alert/alerts-service/alerts.service';

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.css']
})
export class NavigationComponent implements OnInit {

  private currentUser: User;

  constructor(
    private userService: UserService,
    public translate: TranslateService,
    private authenticationService: AuthenticationService,
    private healthService: HealthService,
    private router: Router,
    private alertService: AlertsService
  ) {
    authenticationService.currentUserObservable.subscribe(user => {
      this.currentUser = user;
    });
    let language = localStorage.getItem('language');
    if (language === null || language === undefined) {
      language = 'en';
      localStorage.setItem('language', language);
    }

    translate.setDefaultLang(language);

    interval(15 * 1000).subscribe(eventNumber => {
      this.healthService.getHealthStatus().subscribe();
      const tokenExpirationTime = this.authenticationService.getLoggedInUser().tokenExpirationTime;
      if (tokenExpirationTime != null) {
        if (new Date(tokenExpirationTime) < new Date()) {
          router.navigate(['/login'], {queryParams: {returnUrl: this.router.url}});
          authenticationService.logout();
          alertService.error(this.translate.instant('message.loggedOut'));
        }
      }
    });
  }

  ngOnInit() {
    this.currentUser = this.authenticationService.getLoggedInUser();
  }

  logout() {
    this.authenticationService.logout();
  }

  isUserLoggedIn() {
    return this.authenticationService.isUserLoggedIn();
  }

  switchLanguage = (language: string) => {
    this.translate.use(language);
    localStorage.setItem('language', language);
  };
}
