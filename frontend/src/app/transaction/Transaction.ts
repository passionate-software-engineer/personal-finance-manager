import {Category} from '../category/category';
import {Account} from '../account/account';

export class Transaction {
  id: number;
  date: Date;
  description: string;
  category: Category;
  account: Account;
  price: number;
  editMode = false;
  editedTransaction: Transaction;
}
