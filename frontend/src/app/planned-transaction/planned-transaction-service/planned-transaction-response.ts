export class PlannedTransactionResponse {
  id: number;
  date: Date;
  description: string;
  categoryId: number;
  accountPriceEntries: AccountPriceEntry[];
  isPlanned: boolean;
}

export class AccountPriceEntry {
  accountId: number;
  price: number;
}
