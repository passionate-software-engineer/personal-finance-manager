import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {first} from 'rxjs/operators';

import {AlertsService} from '../alert/alerts-service/alerts.service';
import {AuthenticationService} from '../../authentication/authentication.service';

@Component({templateUrl: 'login.component.html'})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  loading = false;
  submitted = false;
  returnUrl: string;

  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private authenticationService: AuthenticationService,
    private alertService: AlertsService) {
  }

  ngOnInit() {
    this.loginForm = this.formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });

  }

  onSubmit() {
    this.submitted = true;

    if (this.loginForm.invalid) {
      return;
    }

    const returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';

    this.loading = true;
    this.authenticationService.login(this.username(), this.password())
      .pipe(first())
      .subscribe(
        data => {
          this.router.navigate([returnUrl]);
        },
        error => {
          this.alertService.error(error);
          this.loading = false;
        });
  }

  private password() {
    return this.loginForm.controls.password.value;
  }

  private username() {
    return this.loginForm.controls.username.value;
  }
}
