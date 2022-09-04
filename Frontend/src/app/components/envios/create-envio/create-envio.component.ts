import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';

import { UsuarioAuxiliar } from 'src/app/models/auxiliar/usuario-auxiliar';
import { Cliente } from 'src/app/models/cliente';
import { DetalleEnvio } from 'src/app/models/detalle-envio';
import { Envio } from 'src/app/models/envio';
import { Producto } from 'src/app/models/producto';

import { ClienteService } from 'src/app/services/cliente.service';
import { EnvioService } from 'src/app/services/envios/envio.service';
import { ClienteCreateService } from 'src/app/services/facturas/cliente-create.service';
import { ProductoService } from 'src/app/services/producto.service';
import { UsuarioService } from 'src/app/services/usuarios/usuario.service';

import swal from 'sweetalert2';
import { AuthService } from '../../../services/auth.service';
import { Usuario } from '../../../models/usuario';

@Component({
  selector: 'app-create-envio',
  templateUrl: './create-envio.component.html',
  styleUrls: ['./create-envio.component.css']
})
export class CreateEnvioComponent implements OnInit {

  @ViewChild('mybuscar') myBuscarTexto: ElementRef;
  @ViewChild('myEfectivo') myEfectivoRef: ElementRef;
  @ViewChild('myCodProductoChild') myCodProdRef: ElementRef;
  @ViewChild('myCantidadChild') myCantidadRef: ElementRef;
  @ViewChild('myButtonXChild') myButtonXRef: ElementRef;
  @ViewChild('myButton2XChild') myButton2XRef: ElementRef;

  title: string;
  nitIngresado: string;

  envio: Envio;
  producto: Producto;
  cliente: Cliente;
  usuario: Usuario;

  constructor (
    private envioService: EnvioService,
    private productoService: ProductoService,
    private clienteService: ClienteService,
    private clienteCreateService: ClienteCreateService,
    private usuarioService: UsuarioService,
    private authService: AuthService,
    private router: Router
  ) {
    this.title = 'Crear Nuevo Envío';
    this.producto = new Producto();
    this.cliente = new Cliente();
    this.usuario = new Usuario();
    this.envio = new Envio();
  }

  ngOnInit(): void {
    this.cargarVendedor();
  }

  cargarVendedor(): void {
    this.usuario = this.authService.usuario;
  }

  buscarCliente(): void {
    const nit = this.myBuscarTexto.nativeElement.value;
    if (nit) {
      this.clienteService.getClienteByNit(nit).subscribe(
        cliente => {
          this.cliente = cliente;
          // (document.getElementById('codigo')).focus();
          this.myCodProdRef.nativeElement.focus();
        },
        error => {
          if (error.status === 400) {
            swal.fire(`Error: ${error.status}`, 'Petición Equivocada', 'error');
          }
          if (error.status === 404) {
            this.nitIngresado = nit;
            this.clienteCreateService.abrirModal();
          }
        }
      );
    } else {
      swal.fire('NIT Vacío', 'Ingrese un valor valido para realizar la búsqueda.', 'warning');
    }
  }

  cargarCliente(event): void {
    this.cliente = event;
  }

  buscarProducto(): void {
    // const codigo = ((document.getElementById('codigo') as HTMLInputElement)).value;
    const codigo = this.myCodProdRef.nativeElement.value;

    if (codigo) {
      this.productoService.getProductoByCode(codigo).subscribe(
        producto => {
          this.producto = producto;
          // (document.getElementById('cantidad') as HTMLInputElement).focus();
          this.myCantidadRef.nativeElement.focus();
        },
        error => {
          if (error.status === 400) {
            swal.fire(`Error: ${error.status}`, 'Petición no se puede llevar a cabo.', 'error');
          }

          if (error.status === 404) {
            swal.fire(`Error: ${error.status}`, error.error.mensaje, 'error');
          }
        }
      );
    } else {
      swal.fire('Código Inválido', 'Ingrese un código de producto válido para realizar la búsqueda.', 'warning');
    }
  }

  agregarLinea(): void {
    if (!this.cliente) { // Comprueba que el cliente exista
      swal.fire('Ha ocurrido un Problema', 'Por favor, elija un cliente antes de llevar a cabo la venta.', 'error');
    } else {
      if (this.producto) { // comprueba que el producto exista
        const item = new DetalleEnvio();

        // item.cantidad = +((document.getElementById('cantidad') as HTMLInputElement)).value;
        item.cantidad = +(this.myCantidadRef.nativeElement).value;

        if (item.cantidad > this.producto.stock) {
          swal.fire('Stock Insuficiente', 'No existen las suficientes existencias de este producto.', 'warning');
          return;
        } else {
          if (item.cantidad && item.cantidad !== 0) {
            if (this.existeItem(this.producto.idProducto)) {
              this.incrementaCantidad(this.producto.idProducto, item.cantidad);
              this.producto = new Producto();
              // (document.getElementById('cantidad') as HTMLInputElement).value = '';
              this.myCantidadRef.nativeElement.value = '';
            } else {
              item.producto = this.producto;
              item.subTotal = item.calcularImporte();
              this.envio.itemsEnvio.push(item);
              this.producto = new Producto();

              // (document.getElementById('cantidad') as HTMLInputElement).value = '';
              this.myCantidadRef.nativeElement
            }

          } else if (item.cantidad === 0) {
            swal.fire('Cantidad Erronéa', 'La cantidad a agregar debe ser mayor a 0.', 'warning');
          } else if (!item.cantidad) {
            swal.fire('Valor Inválido', 'La cantidad no puede estar vacía.  Ingrese un valor válido.', 'warning');
          }
        }
      }
    }
  }

  existeItem(id: number): boolean {
    let existe = false;
    this.envio.itemsEnvio.forEach((item: DetalleEnvio) => {
      if (id === item.producto.idProducto) {
        existe = true;
      }
    });
    return existe;
  }

  incrementaCantidad(idProducto: number, cantidad: number): void {
    this.envio.itemsEnvio = this.envio.itemsEnvio.map((item: DetalleEnvio) => {
      if (idProducto === item.producto.idProducto) {
        item.cantidad = item.cantidad + cantidad;
        item.subTotal = item.calcularImporte();
      }

      return item;
    });
  }

  eliminarItem(index: number): void {
    this.envio.itemsEnvio.splice(index, 1);
  }

  create(): void {}

  loadProducto(event): void {
    // (document.getElementById('codigo') as HTMLInputElement).value = event.codProducto;
    // this.myButtonXRef.nativeElement.click();
    // (document.getElementById('cantidad') as HTMLInputElement).focus();
    this.myCodProdRef.nativeElement.value = event.codProducto;
    (document.getElementById('button-x')).click();
    this.buscarProducto();
    this.myCantidadRef.nativeElement.focus();
  }

  loadCliente(event): void {
    // (document.getElementById('buscar') as HTMLInputElement).value = event.nit;
    // this.myButton2XRef.nativeElement.click();    
    this.myBuscarTexto.nativeElement.value = event.nit;
    (document.getElementById('button-2x')).click();
    this.buscarCliente();
  }
}
