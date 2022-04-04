import { Router } from '@angular/router';
import { AuthService } from './services/auth.service';
import { HttpHeaders } from '@angular/common/http';

import swal from 'sweetalert2';

export class AppUnauthorizated {

    headers: HttpHeaders = new HttpHeaders().set('Content-Type', 'application/json');

    constructor(
        private router: Router,
        private auth: AuthService
    ) { }

    isNoAutorizado(e): boolean {
        if (e.status === 401) {

            if (this.auth.isAuthenticated()){
                this.auth.logout();
            }
            this.router.navigate(['/login']);
            return true;
        }

        if (e.status === 403){
            swal.fire('Acceso Denegado', 'El usuario no cuenta con las credenciales correctas para acceder a este recurso', 'warning');
            this.router.navigate(['/home']);
            return true;
        }
        return false;
    }

    agregarAuthorizationHeader(authService: AuthService): HttpHeaders {
        const token = authService.token;
        if (token != null) {
            return this.headers.append('Authorization', 'Bearer ' + token);
        }
        return this.headers;
    }
}
