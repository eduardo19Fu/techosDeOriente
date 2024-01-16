import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { Usuario } from 'src/app/models/usuario';

import { AuthService } from 'src/app/services/auth.service';

import swal from 'sweetalert2';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  title: string;
  usuarioTitle: string;
  passwordTitle: string;

  usuario: Usuario;

  constructor(
    private router: Router,
    private authService: AuthService
  ) {
    this.title = 'Iniciar Sesión';
    this.usuarioTitle = 'Usuario';
    this.passwordTitle = 'Password';
    this.usuario = new Usuario();
  }

  ngOnInit(): void {
    this.authService.logout();
  }

  login(): void {

    if (this.usuario.usuario == null || this.usuario.password == null) {
      swal.fire('Error en login', 'Usuario y/o contraseña estan vacíos', 'error');
      return;
    }

    this.authService.login(this.usuario).subscribe(
      response => {
        const payload = JSON.parse(atob(response.access_token.split('.')[1]));

        this.authService.guardarUsuario(response.access_token);
        this.authService.guardarToken(response.access_token);

        window.location.href = '/sitio/home';
      },
      error => {
        if (error.status === 400) {
          swal.fire('Error de Autenticación', 'Usuario y/o contraseña incorrectos', 'error');
        }
      }
    );
  }

}
