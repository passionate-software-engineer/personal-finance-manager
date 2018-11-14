import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ServiceBase} from '../../../helpers/service-base';

const EXPORT_PATH = 'export';
const IMPORT_PATH = 'import';

@Injectable({
  providedIn: 'root'
})
export class ImportExportService extends ServiceBase {

  constructor(http: HttpClient) {
    super(http);
  }

  getExportContent(): Observable<string> {
    return this.http.get<string>(ServiceBase.apiUrl(EXPORT_PATH));
  }

  importContent(content: string): Observable<string> {
    return this.http.post<string>(ServiceBase.apiUrl(IMPORT_PATH), content, this.contentType);
  }

}
