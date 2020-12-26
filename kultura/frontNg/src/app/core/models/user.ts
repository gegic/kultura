import {Authority} from './authority';

export class User {
  id = 0;
  email = '';
  password = '';
  firstName = '';
  lastName = '';
  lastPasswordChange: Date = new Date();
  authorities: Authority[] = [];
  verified = false;

  getRole(): string {
    return this.authorities.find(a => a.authority.startsWith('ROLE'))?.authority.slice(5) ?? '';
  }

}
