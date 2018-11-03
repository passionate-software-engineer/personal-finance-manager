import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Category} from '../category';
import {catchError} from 'rxjs/operators';
import {AlertsService} from '../../alert/alerts-service/alerts.service';
import {ServiceBase} from '../../../helpers/service-base';

const PATH = 'categories';

@Injectable({
  providedIn: 'root'
})
export class CategoryService extends ServiceBase {

  constructor(http: HttpClient, alertService: AlertsService) {
    super(http, alertService);
  }

  private static categoryToCategoryRequest(category: Category) {
    return {
      name: category.name,
      parentCategoryId: category.parentCategory == null ? null : category.parentCategory.id
    };
  }

  getCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(ServiceBase.apiUrl(PATH))
      .pipe(catchError(this.handleError('getCategories', [])));
  }

  addCategory(category: Category): Observable<any> {
    const categoryRequest = CategoryService.categoryToCategoryRequest(category);
    return this.http.post<any>(ServiceBase.apiUrl(PATH), categoryRequest, this.contentType)
      .pipe(catchError(this.handleError('addCategory', [])));
  }

  deleteCategory(id: number): Observable<any> {
    return this.http.delete<any>(ServiceBase.apiUrl(PATH, id))
      .pipe(catchError(this.handleError('deleteCategory', [])));
  }

  editCategory(category: Category): Observable<any> {
    const categoryRequest = CategoryService.categoryToCategoryRequest(category);
    return this.http.put<Category>(ServiceBase.apiUrl(PATH, category.id), categoryRequest, this.contentType)
      .pipe(catchError(this.handleError('editCategory', [])));
  }
}
