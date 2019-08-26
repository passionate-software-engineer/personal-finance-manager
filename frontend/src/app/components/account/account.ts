import {Currency} from './currency';

export class Account {
  id: number;
  name: string;
  balance = 0;
  projectedBalance: number;
  currency: Currency;
  editMode = false;
  lastVerificationDate: Date;
  editedAccount: Account;
  balancePLN = 0;
  archived = false;
}

