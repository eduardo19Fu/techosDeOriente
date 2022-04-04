import { Injectable } from '@angular/core';
import {
  HttpEvent, HttpInterceptor, HttpHandler, HttpRequest
} from '@angular/common/http';

import { Observable, throwError } from 'rxjs';
import { AuthService } from '../../../services/auth.service';

import swal from 'sweetalert2';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';

/** Pass untouched request through to the next request handler. */
@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(
    private auth: AuthService,
    private router: Router
  ) { }

  intercept(req: HttpRequest<any>, next: HttpHandler):
    Observable<HttpEvent<any>> {

    return next.handle(req).pipe(
      catchError(e => {
        if (e.status === 401) {

          if (this.auth.isAuthenticated()) {
            this.auth.logout();
          }
          this.router.navigate(['/login']);
        }

        if (e.status === 403) {
          swal.fire('Acceso Denegado', 'El usuario no cuenta con las credenciales correctas para acceder a este recurso', 'warning');
          this.router.navigate(['/home']);
        }

        return throwError(e);
      })
    );
  }
}
