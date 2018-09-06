import {Component, OnInit} from '@angular/core';
import {UserService} from '../_services/user.service';
import {AuthenticationService} from '../_services/authentication.service';
import {User} from '../_models/user';

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.css']
})
export class NavigationComponent implements OnInit {

  currentUser: User = new User();

  constructor(private userService: UserService, private authenticationService: AuthenticationService) {
    authenticationService.currentUserObservable.subscribe(user => {
      this.currentUser = user;
    });
  }

  ngOnInit() {
    this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
  }

  logout() {
    this.authenticationService.logout();
  }

  isUserLoggedIn() {
    return this.currentUser != null && this.currentUser.id != null;
  }

}
