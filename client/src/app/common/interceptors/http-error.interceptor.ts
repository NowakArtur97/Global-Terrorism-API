import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

@Injectable()
export default class HttpErrorInterceptor implements HttpInterceptor {
  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(
      retry(1),
      catchError((err) => {
        let error = err.error.message || err.statusText || err;
        if (error === 'Unknown Error') {
          error =
            'There was a problem with accessing the page. Please try again in a moment.';
        }
        if (!environment.production) {
          console.log(err);
        }
        return throwError(err);
      })
    );
  }
}
