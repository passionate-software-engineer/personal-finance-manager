import {Category} from '../category/category';
import {Account} from '../account/account';

export class Transaction {
  id: number;
  date: Date;
  description: string;
  category: Category;
  accountPriceEntries: AccountPriceEntry[] = [];
  editMode = false;
  isPlanned = false;
  isRecurrent = false;
  editedTransaction: Transaction;

}

export class AccountPriceEntry {
  account: Account;
  postTransactionAccountBalance = 0;
  price: number;
  pricePLN: number;
}
