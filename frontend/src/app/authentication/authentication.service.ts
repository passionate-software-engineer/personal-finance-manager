import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {map} from 'rxjs/operators';

import {environment} from '../../environments/environment';
import {Subject} from 'rxjs';
import {User} from './user';

@Injectable()
export class AuthenticationService {
  private currentUserSource = new Subject<User>();

  constructor(
    private http: HttpClient
  ) {
  }

  public currentUserObservable = this.currentUserSource.asObservable();

  public updateCurrentUser(user: User) {
    this.currentUserSource.next(user);
  }

  public login(username: string, password: string) {
    return this.http.post<User>(`${environment.apiUrl}/users/authenticate`, {username: username, password: password})
               .pipe(map(user => {
                 // login successful if there's a jwt accessToken in the response

                 if (user && user.accessToken) {
                   // store user details and jwt accessToken in local storage to keep user logged in between page refreshes
                   sessionStorage.setItem('currentUser', JSON.stringify(user));
                   this.updateCurrentUser(user);
                 }

                 return;
               }));
  }

  public logout() {
    // remove user from local storage to log user out
    sessionStorage.removeItem('currentUser');
    this.updateCurrentUser(new User());
  }

  public getLoggedInUser(): User {
    const currentUser = JSON.parse(sessionStorage.getItem('currentUser'));

    if (currentUser == null) {
      return new User();
    }

    return currentUser;
  }

  public isUserLoggedIn(): boolean {
    return (this.getLoggedInUser().accessToken != null) && (this.getLoggedInUser().refreshToken != null);
  }
}
