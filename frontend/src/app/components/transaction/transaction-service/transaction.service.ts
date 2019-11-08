import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Transaction} from '../transaction';
import {TransactionResponse} from './transaction-response';
import {ServiceBase} from '../../../helpers/service-base';
import {DateHelper} from '../../../helpers/date-helper';
import {RecurrencePeriod} from '../recurrence-period';

const PATH = 'transactions';
const SET_AS_RECURRENT = 'setAsRecurrent';
const SET_AS_NOT_RECURRENT = 'setAsNotRecurrent';

@Injectable({
  providedIn: 'root'
})
export class TransactionService extends ServiceBase {

  constructor(http: HttpClient) {
    super(http);
  }

  private static transactionToTransactionRequest(transaction: Transaction) {
    const result = {
      description: transaction.description,
      categoryId: transaction.category.id,
      accountPriceEntries: [],
      date: transaction.date,
      isPlanned: transaction.isPlanned,
      isRecurrent: transaction.isRecurrent,
    };

    for (const entry of transaction.accountPriceEntries) {
      if (entry.account === undefined || entry.price === undefined) {
        continue;
      }

      result.accountPriceEntries.push(
        {
          accountId: entry.account.id,
          price: entry.price
        }
      );
    }

    return result;
  }

  getTransactions(): Observable<TransactionResponse[]> {
    return this.http.get<TransactionResponse[]>(TransactionService.apiUrl(PATH));
  }

  getTransaction(id: number): Observable<TransactionResponse> {
    return this.http.get<TransactionResponse>(ServiceBase.apiUrl(PATH, id));
  }

  addTransaction(transaction: Transaction): Observable<any> {
    if (DateHelper.isFutureDate(transaction.date)) {
      transaction.isPlanned = true;
    }

    const transactionRequest = TransactionService.transactionToTransactionRequest(transaction);
    return this.http.post<any>(ServiceBase.apiUrl(PATH), transactionRequest, this.contentType);
  }

  deleteTransaction(id: number): Observable<any> {
    return this.http.delete<any>(ServiceBase.apiUrl(PATH, id));
  }

  editTransaction(transaction: Transaction): Observable<any> {
    const transactionRequest = TransactionService.transactionToTransactionRequest(transaction);
    return this.http.put<Transaction>(ServiceBase.apiUrl(PATH, transaction.id), transactionRequest, this.contentType);
  }

  commitPlannedTransaction(transaction: Transaction) {
    return this.http.patch<any>(ServiceBase.apiUrl(PATH + '/' + transaction.id), '', this.contentType);
  }

  setAsRecurrent(transaction: Transaction, recurrencePeriod: RecurrencePeriod) {
    const param = new HttpParams().set('recurrencePeriod', recurrencePeriod);
    return this.setRecurrenceStatus(transaction, true, param);
  }

  setAsNotRecurrent(transaction: Transaction) {
    return this.setRecurrenceStatus(transaction, false);
  }

  private setRecurrenceStatus(transaction: Transaction, toBeRecurrent: boolean, param?: HttpParams): Observable<any> {
    const pathEnd = toBeRecurrent ? SET_AS_RECURRENT : SET_AS_NOT_RECURRENT;
    if (param) {
      return this.http.patch<any>(ServiceBase.apiUrl(PATH + '/' + transaction.id + '/' + pathEnd + '?' + param), '', this.contentType);
    } else {
      return this.http.patch<any>(ServiceBase.apiUrl(PATH + '/' + transaction.id + '/' + pathEnd), '', this.contentType);
    }
  }

}
