import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Transaction} from '../transaction';
import {catchError} from 'rxjs/operators';
import {AlertsService} from '../../alert/alerts-service/alerts.service';
import {TransactionResponse} from './transaction-response';
import {ServiceBase} from '../../../helpers/service-base';

const PATH = 'transactions';

@Injectable({
  providedIn: 'root'
})
export class TransactionService extends ServiceBase {

  constructor(http: HttpClient, alertService: AlertsService) {
    super(http, alertService);
  }

  private static transactionToTransactionRequest(transaction: Transaction) {
    const result = {
      description: transaction.description,
      categoryId: transaction.category.id,
      accountPriceEntries: [],
      date: transaction.date
    };

    for (const entry of transaction.accountPriceEntries) {
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
    return this.http.get<TransactionResponse[]>(TransactionService.apiUrl(PATH), this.httpCorrelationId)
      .pipe(catchError(this.handleError('getTransactions', [])));
  }

  getTransaction(id: number): Observable<TransactionResponse> {
    return this.http.get<TransactionResponse>(ServiceBase.apiUrl(PATH, id), this.httpCorrelationId)
      .pipe(catchError(this.handleError('getSingleTransaction', null)));
  }

  addTransaction(transaction: Transaction): Observable<any> {
    const categoryRequest = TransactionService.transactionToTransactionRequest(transaction);
    return this.http.post<any>(ServiceBase.apiUrl(PATH), categoryRequest, this.httpOptions)
      .pipe(catchError(this.handleError('addTransaction', [])));
  }

  deleteTransaction(id: number): Observable<any> {
    return this.http.delete<any>(ServiceBase.apiUrl(PATH, id), this.httpCorrelationId)
      .pipe(catchError(this.handleError('deleteTransaction', [])));
  }

  editTransaction(category: Transaction): Observable<any> {
    const categoryRequest = TransactionService.transactionToTransactionRequest(category);
    return this.http.put<Transaction>(ServiceBase.apiUrl(PATH, category.id), categoryRequest, this.httpOptions)
      .pipe(catchError(this.handleError('editTransaction', [])));
  }
}
