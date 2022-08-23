import { Component, OnInit, AfterViewInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormGroup } from '@angular/forms';

import { AuthService } from '../../../services/auth.service';
import { MovimientosProductoService } from '../../../services/movimientos/movimientos-producto.service';
import { ProductoService } from '../../../services/producto.service';
import { UsuarioService } from '../../../services/usuarios/usuario.service';

import { Producto } from 'src/app/models/producto';
import { MovimientoProducto } from '../../../models/movimiento-producto';
import { UsuarioAuxiliar } from 'src/app/models/auxiliar/usuario-auxiliar';
import { TipoMovimiento } from 'src/app/models/tipo-movimiento';

import { JqueryConfigs } from '../../../utils/jquery/jquery-utils';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-create-movimiento',
  templateUrl: './create-movimiento.component.html',
  styleUrls: ['./create-movimiento.component.css']
})
export class CreateMovimientoComponent implements OnInit, AfterViewInit {

  title: string;
  jQueryConfigs: JqueryConfigs;

  usuario: UsuarioAuxiliar;
  movimientoProducto: MovimientoProducto;
  producto: Producto;
  modalForm: FormGroup;

  productos: Producto[];
  tiposMovimiento: TipoMovimiento[];

  constructor(
    private movimientoProductoService: MovimientosProductoService,
    private productoService: ProductoService,
    private usuarioService: UsuarioService,
    private authService: AuthService,
    private router: Router
  ) {
    this.title = 'Ingresar Nuevo Movimiento';
    this.movimientoProducto = new MovimientoProducto();
    this.producto = new Producto();
    this.jQueryConfigs = new JqueryConfigs();
  }

  ngOnInit(): void {
    this.usuarioService.getUsuario(this.authService.usuario.idUsuario).subscribe(
      usuario => {
        this.usuario = usuario;
        this.movimientoProducto.usuario = this.usuario;
        this.loadTiposMovimiento();
      },
      error => {
        Swal.fire(`Error: ${error.status}`, '', 'error');
      }
    );
  }

  ngAfterViewInit(): void {
    this.jQueryConfigs.hideModal();
    this.producto = new Producto();
  }

  create(): void {
    this.movimientoProducto.producto = this.producto;
    this.movimientoProducto.usuario = this.usuario;
    if (this.movimientoProducto.producto) {
      
      if (this.movimientoProducto.producto.stock >= this.movimientoProducto.cantidad 
            || this.movimientoProducto.tipoMovimiento.tipoMovimiento === 'ENTRADA') {

        this.movimientoProductoService.create(this.movimientoProducto).subscribe(
          response => {
            this.router.navigate(['/productos/inventario/index']);
            Swal.fire('Movimiento creado con éxito', `El movimiento ${response.movimientoProducto.idMovimiento} ha sido creada con éxito!`, 'success');
          },
          error => {
            Swal.fire('Error', error.error, 'error');
          }
        );

      } else {
        Swal.fire('Existencias Insuficientes', 'El stock disponible es insuficiente para surtir la salida', 'warning');
      }
    }
  }

  buscarProducto(): void {
    const codigo = ((document.getElementById('cod-producto') as HTMLInputElement)).value;

    if (codigo) {
      this.productoService.getProductoByCode(codigo).subscribe(
        producto => {
          this.producto = producto;
          (document.getElementById('cantidad') as HTMLInputElement).focus();
        },
        error => {
          if (error.status === 400) {
            Swal.fire(`Error: ${error.status}`, 'Petición no se puede llevar a cabo.', 'error');
          }

          if (error.status === 404) {
            Swal.fire(`Error: ${error.status}`, error.error.mensaje, 'error');
          }
        }
      );
    } else {
      Swal.fire('Código Inválido', 'Ingrese un código de producto válido para realizar la búsqueda.', 'warning');
    }
  }

  loadProducto(event): void {
    (document.getElementById('cod-producto') as HTMLInputElement).value = event.codProducto;
    (document.getElementById('button-x')).click();
    this.buscarProducto();
  }

  loadTiposMovimiento(): void {
    this.movimientoProductoService.getTiposMovimiento().subscribe(
      tiposMovimiento => {
        this.tiposMovimiento = tiposMovimiento.filter(tipoMovimiento => tipoMovimiento.tipoMovimiento === 'ENTRADA' || tipoMovimiento.tipoMovimiento === 'SALIDA');

      }
    );
  }


}
