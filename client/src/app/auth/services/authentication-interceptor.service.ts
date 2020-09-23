import { HttpEvent, HttpHandler, HttpHeaders, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { exhaustMap, map, take } from 'rxjs/operators';
import AppStoreState from 'src/app/store/app.store.state';

@Injectable()
export default class AuthenticationInterceptorService
  implements HttpInterceptor {
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
        const modifiedRequest = req.clone({
          headers: new HttpHeaders().set(
            'Authorization',
            `Bearer ${user.token}`
          ),
        });
        return next.handle(modifiedRequest);
      })
    );
  }
}
