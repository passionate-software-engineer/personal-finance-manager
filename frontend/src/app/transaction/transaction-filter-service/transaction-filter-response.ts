export class FilterResponse {
  id: number;
  name: string;

  dateFrom: Date;
  dateTo: Date;
  description: string;
  categoryIds: number[];
  accountIds: number[];
  priceFrom: number;
  priceTo: number;
}
