import {Injectable} from '@angular/core';
import {NavigationStart, Router} from '@angular/router';
import {Observable, Subject} from 'rxjs';

import {Alert, AlertType} from '../alert';


@Injectable()
export class AlertsService {
  private subject = new Subject<Alert>();
  private keepAfterRouteChange = false;

  constructor(private router: Router) {
    // clear alert messages on route change unless 'keepAfterRouteChange' flag is true
    router.events.subscribe(event => {
      if (event instanceof NavigationStart) {
        if (this.keepAfterRouteChange) {
          this.keepAfterRouteChange = false;
        } else {
          // clear alert messages
          this.clear();
        }
      }
    });
  }

  // subscribe to alerts
  getAlert(): Observable<any> {
    return this.subject.asObservable();
  }

  // convenience methods
  success(message: string) {
    this.alert(new Alert({message, type: AlertType.Success}));
  }

  error(message: string) {
    this.alert(new Alert({message, type: AlertType.Error}));
  }

  info(message: string) {
    this.alert(new Alert({message, type: AlertType.Info}));
  }

  warn(message: string) {
    this.alert(new Alert({message, type: AlertType.Warning}));
  }

  alert(alert: Alert) {
    this.keepAfterRouteChange = alert.keepAfterRouteChange;
    this.subject.next(alert);
  }

  // clear alerts
  clear() {
    this.subject.next();
  }
}
