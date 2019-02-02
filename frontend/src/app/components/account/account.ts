import {Currency} from './currency';

export class Account {
  id: number;
  name: string;
  balance = 0;
  currency: Currency;
  editMode = false;
  lastVerificationDate: Date;
  editedAccount: Account;
}

