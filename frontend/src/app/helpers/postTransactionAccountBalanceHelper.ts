import {Transaction} from '../components/transaction/transaction';
import {Injectable} from '@angular/core';
import {DateHelper} from './date-helper';

@Injectable({
  providedIn: 'root'
})
export class PostTransactionAccountBalanceHelper {

  getPostTransactionBalance(currentAccountId: number, transaction: Transaction): number {
    return this.getValueFromAccountPriceEntry(currentAccountId, transaction, true);
  }

  getPriceFromAccountPriceEntry(currentAccountId: number, transaction: Transaction): number {
    return this.getValueFromAccountPriceEntry(currentAccountId, transaction, false);
  }

  getValueFromAccountPriceEntry(currentAccountId: number, transaction: Transaction, isCalculatingPostTransactionBalance: boolean) {
    const accountPriceEntries = transaction.accountPriceEntries;
    for (let i = 0; i < accountPriceEntries.length; i++) {
      const {account, price, postTransactionAccountBalance} = accountPriceEntries[i];
      if (account.id === currentAccountId) {
        return isCalculatingPostTransactionBalance ? postTransactionAccountBalance : price;
      }
    }
  }

  compareByDateWhenEqualThenCompareByTransactionId(transaction1, transaction2) {
    const compareDatesResult = DateHelper.compareDates(new Date(transaction1.date), new Date(transaction2.date));
    if (compareDatesResult !== 0) {
      return compareDatesResult;
    }
    return transaction1.id < transaction2.id ? -1 : 1;
  }

  getAccountIdToAccountTransactionsMap(transactions: Transaction[]) {
    const accountIdsToTransactionsMap = new Map<number, Transaction[]>();
    for (const transaction of transactions) {
      const accountPriceEntries = transaction.accountPriceEntries;
      for (let i = 0; i < accountPriceEntries.length; i++) {
        const accountId = accountPriceEntries[i].account.id;
        if (accountIdsToTransactionsMap.has(accountId)) {
          const tempTransactions = accountIdsToTransactionsMap.get(accountId);
          tempTransactions.push(transaction);
          accountIdsToTransactionsMap.set(accountId, tempTransactions);
        } else {
          accountIdsToTransactionsMap.set(accountId, [transaction]);
        }
      }
    }
    return accountIdsToTransactionsMap;
  }

  getAccountIdToCurrentAccountBalanceMap(transactions: Transaction[]) {
    const map = new Map<number, number>();
    transactions.forEach(value => {
      const account = value.accountPriceEntries[0].account;
      if (!map.has(account.id)) {
        map.set(account.id, account.balance);
      }
    });
    return map;
  }

  sortTransactionsByDateReverseOrder(transactions: Transaction[]) {
    transactions.sort((transaction1, transaction2) => {
      return this.compareByDateWhenEqualThenCompareByTransactionId(transaction2, transaction1);
    });
    return transactions;
  }

  sortTransactionsByDate(transactions: Transaction[]) {
    transactions.sort((transaction1, transaction2) => {
      return this.compareByDateWhenEqualThenCompareByTransactionId(transaction1, transaction2);
    });
    return transactions;
  }

   calculateAndAssignPostTransactionsBalances(transactions: Transaction[], isCalculatingForPlannedTransactions: boolean) {
    const accountIdToCurrentBalanceMap = this.getAccountIdToCurrentAccountBalanceMap(transactions);
    const accountIdToTransactionsMap = this.getAccountIdToAccountTransactionsMap(transactions);

    accountIdToTransactionsMap.forEach((transactionsByAccountId) => {
      isCalculatingForPlannedTransactions ? this.sortTransactionsByDate(transactionsByAccountId) :
        this.sortTransactionsByDateReverseOrder(transactionsByAccountId);
    });

    accountIdToTransactionsMap.forEach((transactionsByAccountId) => {
        for (let i = 0; i < transactionsByAccountId.length; i++) {
          if (isCalculatingForPlannedTransactions && !transactionsByAccountId[i].isPlanned) {
            throw Error('Unexpected past transaction found while calculating postTransactions balances for planned transactions');
          }
          if (!isCalculatingForPlannedTransactions && transactionsByAccountId[i].isPlanned) {
            throw Error('Unexpected planned transaction found while calculating postTransactions balances for past transactions');
          }

          const accountPriceEntries = transactionsByAccountId[i].accountPriceEntries;
          const accountIdToPreviousPostTransactionBalanceMap = new Map<number, number>();
          for (let j = accountPriceEntries.length - 1; j >= 0; j--) {

            const currentAccountId = accountPriceEntries[j].account.id;
            const isFirstAccountPriceEntryForCurrentAccount = !accountIdToPreviousPostTransactionBalanceMap.has(currentAccountId);
            if (isFirstAccountPriceEntryForCurrentAccount) {
              const transactionsByAccount = accountIdToTransactionsMap.get(currentAccountId);
              const currentTransactionIndexInTransactionsByAccount = accountIdToTransactionsMap.get(currentAccountId)
                                                                                               .indexOf(transactionsByAccountId[i]);
              const isTheMostRecentTransactionOnAccount = currentTransactionIndexInTransactionsByAccount === 0;

              if (isTheMostRecentTransactionOnAccount) {
                const accountBalanceFactor = isCalculatingForPlannedTransactions ? accountPriceEntries[j].price : 0;
                accountPriceEntries[j].postTransactionAccountBalance = +accountIdToCurrentBalanceMap.get(currentAccountId) + accountBalanceFactor;
                accountIdToPreviousPostTransactionBalanceMap.set(currentAccountId, accountPriceEntries[j].postTransactionAccountBalance);

              } else {
                const previousTransactionOnAccount = transactionsByAccount[currentTransactionIndexInTransactionsByAccount - 1];
                const previousPostTransactionAccountBalance = this.getPostTransactionBalance(currentAccountId, previousTransactionOnAccount);
                const notMostRecentTransactionOnAccountFactor = isCalculatingForPlannedTransactions ? 0 : 1;
                const previousAccountPriceEntryPrice = this.getPriceFromAccountPriceEntry(currentAccountId, transactionsByAccount
                  [currentTransactionIndexInTransactionsByAccount - notMostRecentTransactionOnAccountFactor]);
                const transactionTypeDependentFactor = isCalculatingForPlannedTransactions ? accountPriceEntries[j].price
                  : -previousAccountPriceEntryPrice;

                accountPriceEntries[j].postTransactionAccountBalance = previousPostTransactionAccountBalance + transactionTypeDependentFactor;

                accountIdToPreviousPostTransactionBalanceMap.set(currentAccountId, accountPriceEntries[j].postTransactionAccountBalance);
              }
            } else {
              const previousPostTransactionAccountBalance = accountIdToPreviousPostTransactionBalanceMap.get(currentAccountId);
              const notFirstAccountPriceEntryFactor = isCalculatingForPlannedTransactions ? accountPriceEntries[j].price
                : -accountPriceEntries[j + 1].price;

              accountPriceEntries[j].postTransactionAccountBalance = previousPostTransactionAccountBalance + notFirstAccountPriceEntryFactor;

              accountIdToPreviousPostTransactionBalanceMap.set(currentAccountId, accountPriceEntries[j].postTransactionAccountBalance);
            }
          }
        }
      }
    );
  }
}
