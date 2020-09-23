import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { NgModule } from '@angular/core';

import AuthenticationInterceptorService from './auth/services/authentication-interceptor.service';

@NgModule({
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthenticationInterceptorService,
      multi: true,
    },
  ],
})
export class CoreModule {}
