import {Category} from '../category/category';
import {Account} from '../account/account';

export class TransactionFilter {
  id: number;
  name: string;

  dateFrom: string; // date input is using string
  dateTo: string; // date input is using string
  description: string;
  categories: Category[];
  accounts: Account[];
  priceFrom: number;
  priceTo: number;
}
