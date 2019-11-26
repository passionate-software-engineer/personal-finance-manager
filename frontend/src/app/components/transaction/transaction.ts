import {Category} from '../category/category';
import {Account} from '../account/account';
import {RecurrencePeriod} from './recurrence-period';

export class Transaction {
  id: number;
  date: Date;
  description: string;
  category: Category;
  accountPriceEntries: AccountPriceEntry[] = [];
  editMode = false;
  isPlanned = false;
  isRecurrent = false;
  recurrencePeriod = RecurrencePeriod.NONE;
  editedTransaction: Transaction;
}


export class AccountPriceEntry {
  account: Account;
  price: number;
  pricePLN: number;
}
