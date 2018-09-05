import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {map} from 'rxjs/operators';

import {environment} from '../../environments/environment';
import {User} from '../_models';
import {Subject} from 'rxjs';

@Injectable()
export class AuthenticationService {
  private currentUserSource = new Subject<User>();

  // Observable string streams
  userChanged = this.currentUserSource.asObservable();

  constructor(private http: HttpClient) {
  }

  updateUser(user: User) {
    this.currentUserSource.next(user);
  }

  login(username: string, password: string) {
    return this.http.post<any>(`${environment.apiUrl}/users/authenticate`, {username: username, password: password})
      .pipe(map(user => {
        // login successful if there's a jwt token in the response
        if (user && user.token) {
          // store user details and jwt token in local storage to keep user logged in between page refreshes
          localStorage.setItem('currentUser', JSON.stringify(user));
        }

        this.updateUser(user);
        return user;
      }));
  }

  logout() {
    // remove user from local storage to log user out
    localStorage.removeItem('currentUser');
    this.updateUser(new User());
  }
}
