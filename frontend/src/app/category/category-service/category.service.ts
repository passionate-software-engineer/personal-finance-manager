import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { Category } from '../category';
import { MessagesService } from '../../messages/messages.service';
import { catchError, map, tap } from 'rxjs/operators';
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};
@Injectable({
  providedIn: 'root'
})
export class CategoryService {

  private apiUrl = 'http://localhost:8081/categories';

  constructor(private http: HttpClient, private messagesService: MessagesService) { }

  getCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(this.apiUrl).pipe(
      tap(categories => this.log(`fetched categories`)),
      catchError(this.handleError('getCategories', [])));
  }

  addCategory(category: Category): Observable<any> {
    return this.http.post<any>(this.apiUrl, category, httpOptions).pipe(
      tap(any => this.log(`added category with id: ` + any)),
      catchError(this.handleError('addCategory', [])));
  }

  deleteCategory(id: number): Observable<any> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.delete<any>(url).pipe(
      tap(any => this.log(`deleted category with id: ` + id)),
      catchError(this.handleError('deleteCategory', [])));
  }

  editCategory(category: Category): Observable<any> {
    const url = `${this.apiUrl}/${category.id}`;
    return this.http.put<Category>(url, category, httpOptions).pipe(
      tap(() => this.log(`edited category with id: ` + category.id)),
      catchError(this.handleError('editCategory', [])));
  }

  private log(message: string) {
    this.messagesService.add('CategoryService: ' + message);
  }

  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {

      // TODO: send the error to remote logging infrastructure
      console.error(error); // log to console instead

      // TODO: better job of transforming error for user consumption
      this.log(`${operation} failed: ${error.message}`);

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }
}
