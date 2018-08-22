import {Category} from '../category/category';
import {Account} from '../account/account';

export class TransactionFilter {
  id: number;
  name: string;

  dateFrom: Date;
  dateTo: Date;
  description: string;
  categories: Category[];
  accounts: Account[];
  priceFrom: number;
  priceTo: number;
}
