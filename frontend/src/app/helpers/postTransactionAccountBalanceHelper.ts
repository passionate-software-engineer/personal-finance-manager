import {Transaction} from '../components/transaction/transaction';
import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class PostTransactionAccountBalanceHelper {
  private static getPostTransactionBalance(currentAccountId: number, transaction: Transaction): number {
    return PostTransactionAccountBalanceHelper.get_APE_Value(currentAccountId, transaction, true);
  }

  private static get_APE_Price(currentAccountId: number, transaction: Transaction): number {
    return PostTransactionAccountBalanceHelper.get_APE_Value(currentAccountId, transaction, false);
  }

  private static get_APE_Value(currentAccountId: number, transaction: Transaction, postTransactionBalance: boolean) {
    const apes = transaction.accountPriceEntries;
    for (let i = 0; i < apes.length; i++) {
      if (apes[i].account.id === currentAccountId) {
        return postTransactionBalance ? apes[i].postTransactionAccountBalance : apes[i].price;
      }
    }
  }

  private static compareByDateWhenEqualThenCompareByTransactionId(transaction1, transaction2) {
    if (transaction1.date < transaction2.date) {
      return -1;
    }
    if (transaction1.date > transaction2.date) {
      return 1;
    }
    if (transaction1.id < transaction2.id) {
      return -1;
    } else {
      return 1;
    }
  }

  getAccountIdToAccountTransactionsMap(transactions: Transaction[]) {
    const accountIdsToTransactionsMap = new Map<number, Transaction[]>();
    for (const transaction of transactions) {
      const APEs = transaction.accountPriceEntries;
      for (let i = 0; i < APEs.length; i++) {
        const accountId = APEs[i].account.id;
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
      return PostTransactionAccountBalanceHelper.compareByDateWhenEqualThenCompareByTransactionId(transaction2, transaction1);
    });
    return transactions;
  }

  sortTransactionsByDate(transactions: Transaction[]) {
    transactions.sort((transaction1, transaction2) => {
      return PostTransactionAccountBalanceHelper.compareByDateWhenEqualThenCompareByTransactionId(transaction1, transaction2);
    });
    return transactions;
  }

  calculateAndAssignPostTransactionBalancesForTransactions(transactions: Transaction[]) {
    this.calculateAndAssignBalances(transactions, false);
  }

  calculateAndAssignPostTransactionBalancesForPlannedTransactions(transactions: Transaction[]) {
    this.calculateAndAssignBalances(transactions, true);
  }

  private calculateAndAssignBalances(transactions: Transaction[], calculateForPlannedTransactions: boolean) {
    const accountIdToCurrentAccountBalanceMap = this.getAccountIdToCurrentAccountBalanceMap(transactions);
    const accountIdToAccountTransactionsMap = this.getAccountIdToAccountTransactionsMap(transactions);

    accountIdToAccountTransactionsMap.forEach((transactionsByAccountId) => {
      calculateForPlannedTransactions ? this.sortTransactionsByDate(transactionsByAccountId) :
          this.sortTransactionsByDateReverseOrder(transactionsByAccountId);
    });

    accountIdToAccountTransactionsMap.forEach((transaction) => {
          // iterate over transactions
          for (let i = 0; i < transaction.length; i++) {
            const apes = transaction[i].accountPriceEntries;

            const accountIdToPrevious_APE_BalanceWithinTheSameTransactionMap = new Map<number, number>();
            const accountIdToLastPriceInSameTransactionMap = new Map<number, number>();

            // iterate over APEs of current transaction
            for (let j = 0; j < apes.length; j++) {
              const current_APE_accountId = apes[j].account.id;

              if (!accountIdToPrevious_APE_BalanceWithinTheSameTransactionMap.has(current_APE_accountId)) {

                const transactionsBy_APE_accountId = accountIdToAccountTransactionsMap.get(current_APE_accountId);
                const currentTransactionInTransactionsByCurrentAccountIndex = accountIdToAccountTransactionsMap.get(current_APE_accountId).
                                                                                                                indexOf(transaction[i]);
                const isTheFirstTransactionOnCurrentAccount = currentTransactionInTransactionsByCurrentAccountIndex === 0;

                if (isTheFirstTransactionOnCurrentAccount) {
                  const plannedTransactionsFactor = calculateForPlannedTransactions ? apes[j].price : 0;
                  apes[j].postTransactionAccountBalance = +accountIdToCurrentAccountBalanceMap.get(current_APE_accountId) + plannedTransactionsFactor;
                } else if (currentTransactionInTransactionsByCurrentAccountIndex > 0) {

                  const previousPostTransactionAccountBalance = PostTransactionAccountBalanceHelper.getPostTransactionBalance(current_APE_accountId,
                      transactionsBy_APE_accountId[currentTransactionInTransactionsByCurrentAccountIndex - 1]);
                  const plannedTransactionsBalanceFactor = calculateForPlannedTransactions ? 0 : 1;

                  const previousTransactionPrice = PostTransactionAccountBalanceHelper.get_APE_Price(current_APE_accountId,
                      transactionsBy_APE_accountId[currentTransactionInTransactionsByCurrentAccountIndex - plannedTransactionsBalanceFactor]);
                  const plannedTransactionsPriceFactor = calculateForPlannedTransactions ? previousTransactionPrice : -previousTransactionPrice;

                  apes[j].postTransactionAccountBalance = +previousPostTransactionAccountBalance + plannedTransactionsPriceFactor;
                  accountIdToPrevious_APE_BalanceWithinTheSameTransactionMap.set(current_APE_accountId, apes[j].postTransactionAccountBalance);
                  accountIdToLastPriceInSameTransactionMap.set(current_APE_accountId, apes[j].price);
                }
              } else {
                const previousPostTransactionAccountBalance = accountIdToPrevious_APE_BalanceWithinTheSameTransactionMap.get(current_APE_accountId);
                apes[j].postTransactionAccountBalance = previousPostTransactionAccountBalance + apes[j].price;
                accountIdToPrevious_APE_BalanceWithinTheSameTransactionMap.set(current_APE_accountId, apes[j].postTransactionAccountBalance);
              }
            }
          }
        }
    );
  }
}
