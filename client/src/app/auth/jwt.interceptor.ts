import {
  HttpEvent,
  HttpHandler,
  HttpHeaders,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { EMPTY, Observable } from 'rxjs';
import { exhaustMap, map, take } from 'rxjs/operators';
import AppStoreState from 'src/app/store/app.state';

@Injectable()
export default class JwtInterceptor implements HttpInterceptor {
  constructor(private store: Store<AppStoreState>) {}

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    return this.store.select('auth').pipe(
      take(1),
      map((authState) => authState.user),
      exhaustMap((user) => {
        if (
          req.url.includes('authentication') ||
          req.url.includes('registration')
        ) {
          return next.handle(req);
        } else if (!user) {
          return EMPTY;
        } else {
          const modifiedRequest = req.clone({
            headers: new HttpHeaders().set(
              'Authorization',
              `Bearer ${user.token}`
            ),
          });
          return next.handle(modifiedRequest);
        }
      })
    );
  }
}
