export class TransactionResponse {
  id: number;
  date: Date;
  description: string;
  categoryId: number;
  accountPriceEntries: AccountPriceEntry[];
}

export class AccountPriceEntry {
  accountId: number;
  price: number;
}
