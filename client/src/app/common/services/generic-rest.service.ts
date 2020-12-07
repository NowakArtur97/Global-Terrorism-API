import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

export default abstract class GenericRestService<T> {
  protected readonly DEFAULT_PAGE_SIZE = 200;

  constructor(protected httpClient: HttpClient, protected actionUrl: string) {}

  getAll(pageSize: number = this.DEFAULT_PAGE_SIZE): Observable<T> {
    return this.httpClient.get<T>(
      `${environment.baseApiUrl}/${this.actionUrl}?page=0&size=${pageSize}`
    );
  }
}
