import {Component, OnInit} from '@angular/core';
import {User} from '../../authentication/user';
import {UserService} from '../../authentication/user.service';
import {AuthenticationService} from '../../authentication/authentication.service';

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.css']
})
export class NavigationComponent implements OnInit {

  private currentUser: User;

  constructor(private userService: UserService, private authenticationService: AuthenticationService) {
    authenticationService.currentUserObservable.subscribe(user => {
      this.currentUser = user;
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

}
