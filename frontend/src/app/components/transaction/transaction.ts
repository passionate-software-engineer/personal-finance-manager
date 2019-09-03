import {Category} from '../category/category';
import {Account} from '../account/account';

export class Transaction {
  constructor(isPlanned: boolean) {
    this.isPlanned = isPlanned;
  }
  id: number;
  date: Date;
  description: string;
  category: Category;
  accountPriceEntries: AccountPriceEntry[] = [];
  editMode = false;
  isPlanned = false;
  editedTransaction: Transaction;
}


export class AccountPriceEntry {
  account: Account;
  price: number;
  pricePLN: number;
}
