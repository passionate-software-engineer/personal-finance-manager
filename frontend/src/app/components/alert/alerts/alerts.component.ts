import {Component, OnInit} from '@angular/core';
import {Alert, AlertType} from '../alert';
import {AlertsService} from '../alerts-service/alerts.service';

@Component({
  selector: 'app-alerts',
  templateUrl: 'alerts.component.html',
  styleUrls: ['alerts.component.css']
})

export class AlertsComponent implements OnInit {
  alerts: Alert[] = [];

  constructor(private alertService: AlertsService) {
  }

  ngOnInit() {
    this.alertService.getAlert()
        .subscribe((alert: Alert) => {
          if (!alert) {
            // clear alerts when an empty alert is received
            this.alerts = [];
            return;
          }
          if (alert.type === AlertType.Error) {
            setTimeout(() => {
              if (this.alerts && this.alerts.length > 0) {
                this.alerts.shift();
              }
            }, 10000);
          } else {
            setTimeout(() => {
              if (this.alerts && this.alerts.length > 0) {
                this.alerts.shift();
              }
            }, 3000);
          }

          // add alert to array
          this.alerts.push(alert);
        });
  }

  removeAlert(alert: Alert) {
    this.alerts = this.alerts.filter(x => x !== alert);
  }

  cssClass(alert: Alert) {
    if (!alert) {
      return;
    }

    // return css class based on alert type
    switch (alert.type) {
      case AlertType.Success:
        return 'alert alert-success';
      case AlertType.Error:
        return 'alert alert-danger';
      case AlertType.Info:
        return 'alert alert-info';
      case AlertType.Warning:
        return 'alert alert-warning';
    }
  }
}
