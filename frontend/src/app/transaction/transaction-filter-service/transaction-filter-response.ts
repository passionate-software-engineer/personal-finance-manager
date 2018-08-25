export class FilterResponse {
  id: number;
  name: string;

  dateFrom: string; // date input is using string
  dateTo: string; // date input is using string
  description: string;
  categoryIds: number[];
  accountIds: number[];
  priceFrom: number;
  priceTo: number;
}
