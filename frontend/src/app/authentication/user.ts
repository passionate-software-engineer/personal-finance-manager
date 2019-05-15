/**
 * Update this class to store refresh accessToken
 */
export class User {
  id: number;
  username: string;
  password: string;
  firstName: string;
  lastName: string;
  accessToken: string;
  accessTokenExpirationTime: string;
  refreshToken: string;
  refershTokenExpirationTime: string;
}
