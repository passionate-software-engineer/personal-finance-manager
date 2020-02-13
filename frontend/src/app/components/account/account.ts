import {Currency} from './currency';
import {AccountType} from './type';

export class Account {
  id: number;
  name: string;
  type: AccountType;
  balance = 0;
  currency: Currency;
  editMode = false;
  lastVerificationDate: Date;
  editedAccount: Account;
  balancePLN = 0;
  archived = false;
}

