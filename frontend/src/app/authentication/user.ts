import {Token} from './token';

export class User {
  id: number;
  username: string;
  password: string;
  firstName: string;
  lastName: string;
  accessToken: Token;
  refreshToken: Token;
}
