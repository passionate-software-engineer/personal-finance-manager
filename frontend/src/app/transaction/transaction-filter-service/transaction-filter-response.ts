export class FilterResponse {
  id: number;
  name: string;

  dateFrom: Date;
  dateTo: Date;
  description: string;
  categories: number[];
  accounts: number[];
  priceFrom: number;
  priceTo: number;
}
