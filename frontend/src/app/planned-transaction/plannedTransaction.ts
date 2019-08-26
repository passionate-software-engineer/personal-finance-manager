import {Category} from '../components/category/category';
import {Account} from '../components/account/account';

export class PlannedTransaction {
  id: number;
  date: Date;
  description: string;
  category: Category;
  accountPriceEntries: AccountPriceEntry[] = [];
  editMode = false;
  editedPlannedTransaction: PlannedTransaction;
}

export class AccountPriceEntry {
  account: Account;
  price: number;
  pricePLN: number;
}
