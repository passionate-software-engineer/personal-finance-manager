export class TransactionResponse {
  id: number;
  date: Date;
  description: string;
  categoryId: number;
  accountPriceEntries: AccountPriceEntry[];
  planned: boolean;
}

export class AccountPriceEntry {
  accountId: number;
  price: number;
}
