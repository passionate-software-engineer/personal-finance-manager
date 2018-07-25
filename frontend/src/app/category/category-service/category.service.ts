import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable, of, throwError} from 'rxjs';
import {Category} from '../category';
import {MessagesService} from '../../messages/messages.service';
import {catchError, tap} from 'rxjs/operators';
import {environment} from '../../../environments/environment';

const httpOptions = {
  headers: new HttpHeaders({'Content-Type': 'application/json'})
};

@Injectable({
  providedIn: 'root'
})
export class CategoryService {

  private apiUrl = environment.appUrl + '/categories';

  constructor(private http: HttpClient, private messagesService: MessagesService) {
  }

  private static categoryToCategoryRequest(category: Category) {
    return {
      name: category.name,
      parentCategoryId: category.parentCategory == null ? null : category.parentCategory.id
    };
  }

  getCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(this.apiUrl).pipe(
      tap(() => this.log(`fetched categories`)),
      catchError(this.handleError('getCategories', [])));
  }

  addCategory(category: Category): Observable<any> {
    const categoryRequest = CategoryService.categoryToCategoryRequest(category);
    return this.http.post<any>(this.apiUrl, categoryRequest, httpOptions).pipe(
      tap(any => this.log(`added category with id: ` + any)),
      catchError(this.handleError('addCategory', [])));
  }

  deleteCategory(id: number): Observable<any> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.delete<any>(url).pipe(
      tap(() => this.log(`deleted category with id: ` + id)),
      catchError(this.handleError('deleteCategory', [])));
  }

  editCategory(category: Category): Observable<any> {
    const categoryRequest = CategoryService.categoryToCategoryRequest(category);
    const url = `${this.apiUrl}/${category.id}`;
    return this.http.put<Category>(url, categoryRequest, httpOptions).pipe(
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
      this.log(`${operation} failed: ${error.message}  `);
      this.log(`${operation} failed: ${JSON.stringify(error)}  `);

      // Let the app keep running by returning an empty result.
      return throwError(error);
    };
  }
}
