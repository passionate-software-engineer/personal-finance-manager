import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';

import {environment} from '../../environments/environment';
import {User} from './user';
import {Token} from './token';

@Injectable()
export class UserService {

  constructor(private http: HttpClient) {
  }

  register(user: User) {
    return this.http.post(`${environment.apiUrl}/users/register`, user);
  }

  extendToken(token: Token) {
    return this.http.post<Token>(`${environment.apiUrl}/users/refresh`, token);
  }

}
