import { Component, OnInit } from '@angular/core';
import { Compra } from '../../../models/compra';
import { Router } from '@angular/router';

import { AuthService } from '../../../services/auth.service';
import { CompraService } from '../../../services/compras/compra.service';

import Swal from 'sweetalert2';
import { TipoComprobante } from '../../../models/tipo-comprobante';
import { UsuarioAuxiliar } from '../../../models/auxiliar/usuario-auxiliar';
import { ProveedorService } from '../../../services/proveedores/proveedor.service';
import { Proveedor } from '../../../models/proveedor';
import { UsuarioService } from '../../../services/usuarios/usuario.service';

@Component({
  selector: 'app-create-compra',
  templateUrl: './create-compra.component.html',
  styles: [
  ]
})
export class CreateCompraComponent implements OnInit {

  title: string;

  compra: Compra;
  usuario: UsuarioAuxiliar;

  tiposComprobante: TipoComprobante[];
  proveedores: Proveedor[];


  constructor(
    private router: Router,
    private compraService: CompraService,
    private proveedorService: ProveedorService,
    private usuarioService: UsuarioService,
    private authService: AuthService
  ) {
    this.compra = new Compra();
    this.title = 'Nueva Compra';
  }

  ngOnInit(): void {
    this.loadTiposComprobante();
    this.loadProveedores();
  }

  create(): void {
    this.usuarioService.getUsuario(this.authService.usuario.idUsuario).subscribe(
      response => {
        this.compra.usuario = response;
        if (this.compra.usuario)
        this.compraService.create(this.compra).subscribe(
          response => {
            this.router.navigate(['/compras/index']);
            Swal.fire(response.mensaje, `La compra: ${response.compra.noComprobante} fue guardada con éxito.`, 'success');
          }
        );
      }  
    );
  }

  loadTiposComprobante(): void {
    this.compraService.getTiposComprobante().subscribe(
      tiposComprobante => {
        this.tiposComprobante = tiposComprobante;
      }
    );
  }

  loadProveedores(): void {
    this.proveedorService.getProveedores().subscribe(
      proveedores => this.proveedores = proveedores
    );
  }

}
