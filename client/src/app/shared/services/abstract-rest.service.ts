import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

export default abstract class RestAbstractService<T> {
  private readonly DEFAULT_PAGE_SIZE = 50;

  constructor(protected httpClient: HttpClient, protected actionUtl: string) {}

  getAll(pageSize: number = this.DEFAULT_PAGE_SIZE): Observable<T> {
    return this.httpClient.get<T>(
      `${environment.baseApiUrl}/${this.actionUtl}?page=0&size=${pageSize}`
    );
  }
}
