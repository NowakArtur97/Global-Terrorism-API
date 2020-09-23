import { HttpEvent, HttpHandler, HttpInterceptor, HttpParams, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { exhaustMap, map, take } from 'rxjs/operators';
import AppStoreState from 'src/app/store/app.store.state';

@Injectable()
export class AuthenticationInterceptorService implements HttpInterceptor {
  constructor(private store: Store<AppStoreState>) {}

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    return this.store.select('auth').pipe(
      take(1),
      map((authState) => {
        return authState.user;
      }),
      exhaustMap((user) => {
        if (!user) {
          return next.handle(req);
        }
        const modifiedReq = req.clone({
          params: new HttpParams().set(
            'Authorization',
            `Bearer  ${user.token}`
          ),
        });
        return next.handle(modifiedReq);
      })
    );
  }
}
