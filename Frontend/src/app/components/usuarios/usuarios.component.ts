import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Usuario } from 'src/app/models/usuario';
import { AuthService } from 'src/app/services/auth.service';
import { DetailUsuarioService } from 'src/app/services/usuarios/detail-usuario.service';
import { UsuarioService } from 'src/app/services/usuarios/usuario.service';

import swal from 'sweetalert2';
import { JqueryConfigs } from '../../utils/jquery/jquery-utils';

@Component({
  selector: 'app-usuarios',
  templateUrl: './usuarios.component.html',
  styles: [
  ]
})
export class UsuariosComponent implements OnInit {

  title: string;
  usuarios: Usuario[];
  usuarioSeleccionado: Usuario;

  jQueryConfigs: JqueryConfigs;

  swalWithBootstrapButtons = swal.mixin({
    customClass: {
      confirmButton: 'btn btn-success',
      cancelButton: 'btn btn-danger'
    },
    buttonsStyling: true
  });

  constructor(
    private detailUsuarioService: DetailUsuarioService,
    private usuarioService: UsuarioService,
    private activatedRoute: ActivatedRoute,
    private authService: AuthService
  ) {
    this.title = 'Listado de Usuarios';
    this.jQueryConfigs = new JqueryConfigs();
  }

  ngOnInit(): void {
    this.getUsuarios();
  }

  getUsuarios(): void {
    this.usuarioService.getUsuarios().subscribe(usuarios => {
      this.usuarios = usuarios;
      this.jQueryConfigs.configDataTable('usuarios');
      this.jQueryConfigs.configToolTip();
    });
  }

  delete(usuario: Usuario): void {
    this.swalWithBootstrapButtons.fire({
      title: '¿Está seguro?',
      text: `¿Seguro que desea eliminar el usuario ${usuario.usuario}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: '¡Si, eliminar!',
      cancelButtonText: '¡No, cancelar!',
      reverseButtons: true
    }).then((result) => {
      if (result.isConfirmed) {

        // tslint:disable-next-line: deprecation
        this.usuarioService.delete(usuario.idUsuario).subscribe(
          response => {
            if (this.authService.usuario.idUsuario === usuario.idUsuario) {
              this.authService.logout();
            } else {
              this.usuarios = this.usuarios.filter(us => us !== usuario);
              this.swalWithBootstrapButtons.fire(
                '¡Usuario eliminado con éxito!',
                'El registro ha sido eliminado con éxito!',
                'success'
              );
            }
          }
        );
      } else if (
        /* Read more about handling dismissals below */
        result.dismiss === swal.DismissReason.cancel
      ) {
        this.swalWithBootstrapButtons.fire(
          'Proceso Cancelado',
          'El registro no fué eliminado de la base de datos.',
          'error'
        );
      }
    });
  }

  abrirDetalle(usuario: Usuario): void{
    this.usuarioSeleccionado = usuario;
    this.detailUsuarioService.abrirModal();
  }

}
