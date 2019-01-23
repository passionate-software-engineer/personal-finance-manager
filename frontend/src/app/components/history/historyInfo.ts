export class HistoryInfo {
  id: number;
  name: string;
  newValue: string;
  oldValue: string;
  newAccountPriceEntries: HistoryAccountPriceEntry[];
  oldAccountPriceEntries: HistoryAccountPriceEntry[];
}

export class HistoryAccountPriceEntry {
  accountName: string;
  amount: number;
}
