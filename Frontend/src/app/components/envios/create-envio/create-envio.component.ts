import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';

import { UsuarioAuxiliar } from '../../../models/auxiliar/usuario-auxiliar';
import { Cliente } from '../../../models/cliente';
import { DetalleEnvio } from '../../../models/detalle-envio';
import { Envio } from '../../../models/envio';
import { Producto } from '../../../models/producto';
import { Usuario } from '../../../models/usuario';

import { ClienteService } from '../../../services/cliente.service';
import { EnvioService } from '../../../services/envios/envio.service';
import { ClienteCreateService } from '../../../services/facturas/cliente-create.service';
import { ProductoService } from '../../../services/producto.service';
import { UsuarioService } from '../../../services/usuarios/usuario.service';
import { AuthService } from '../../../services/auth.service';

import swal from 'sweetalert2';

@Component({
  selector: 'app-create-envio',
  templateUrl: './create-envio.component.html',
  styleUrls: ['./create-envio.component.css']
})
export class CreateEnvioComponent implements OnInit {

  @ViewChild('mybuscar') myBuscarTexto: ElementRef;
  @ViewChild('myAbono') myAbonoRef: ElementRef;
  @ViewChild('myCodProductoChild') myCodProdRef: ElementRef;
  @ViewChild('myCantidadChild') myCantidadRef: ElementRef;
  @ViewChild('myButtonXChild') myButtonXRef: ElementRef;
  @ViewChild('myButton2XChild') myButton2XRef: ElementRef;
  @ViewChild('mySaldoChild') mySaldoRef: ElementRef;

  title: string;
  nitIngresado: string;
  abono: number;
  saldoRestante = 0.00;

  envio: Envio;
  producto: Producto;
  cliente: Cliente;
  usuario: UsuarioAuxiliar;

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
    this.usuario = new UsuarioAuxiliar();
    this.envio = new Envio();
  }

  ngOnInit(): void {
    this.cargarVendedor();
  }

  cargarVendedor(): void {
    this.usuario.idUsuario = this.authService.usuario.idUsuario;
    this.usuario.primerNombre = this.authService.usuario.primerNombre;
    this.usuario.apellido = this.authService.usuario.apellido;
    this.usuario.usuario = this.authService.usuario.usuario;
  }

  buscarCliente(): void {
    const nit = this.myBuscarTexto.nativeElement.value;
    if (nit) {
      this.clienteService.getClienteByNit(nit).subscribe(
        cliente => {
          this.cliente = cliente;
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
    const codigo = this.myCodProdRef.nativeElement.value;

    if (codigo) {
      this.productoService.getProductoByCode(codigo).subscribe(
        producto => {
          this.producto = producto;
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

  buscarProductoPorID(id: number): void {
    this.productoService.getProducto(id).subscribe(
      producto => {
        this.producto = producto;(document.getElementById('cantidad') as HTMLInputElement).focus();
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
  }

  agregarLinea(): void {
    if (!this.cliente) { // Comprueba que el cliente exista
      swal.fire('Ha ocurrido un Problema', 'Por favor, elija un cliente antes de llevar a cabo el envío.', 'error');
    } else {
      if (this.producto) { // comprueba que el producto exista
        const item = new DetalleEnvio();
        item.cantidad = +(this.myCantidadRef.nativeElement).value;
        item.descuento = 0;

        if (item.cantidad > this.producto.stock) {
          swal.fire('Stock Insuficiente', 'No existen las suficientes existencias de este producto.', 'warning');
          return;
        } else {
          if (item.cantidad && item.cantidad !== 0) {
            if (this.existeItem(this.producto.idProducto)) {
              this.incrementaCantidad(this.producto.idProducto, item.cantidad);
              this.producto = new Producto();
              this.myCantidadRef.nativeElement.value = '';
            } else {
              item.producto = this.producto;
              item.subTotalDescuento = item.calcularImporte();
              item.subTotal = item.calcularImporte();
              this.envio.itemsEnvio.push(item);
              this.producto = new Producto();

              this.myCantidadRef.nativeElement.value = '';
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

  actualizarCantidad(idProducto: number, event: any): void {
    const cantidad = event.target.value as number;

    this.envio.itemsEnvio = this.envio.itemsEnvio.map((item: DetalleEnvio) => {
      if (idProducto === item.producto.idProducto) {
        if (cantidad > item.producto.stock) {
          swal.fire('Stock Insuficiente', 'No existen las suficientes existencias de este producto.', 'warning');
        } else {
          item.cantidad = cantidad;
          item.subTotal = item.calcularImporte();
          item.subTotalDescuento = item.calcularImporteDescuento();
        }
      }

      return item;
    });
  }

  actualizarCantidadDescuento(idProducto: number, event: any): void {
    const descuento = event.target.value as number;

    this.envio.itemsEnvio = this.envio.itemsEnvio.map((item: DetalleEnvio) => {
      if (idProducto === item.producto.idProducto) {
        item.descuento = descuento;
        item.subTotal = item.calcularImporte();
        item.subTotalDescuento = item.calcularImporteDescuento();
      }

      return item;
    });
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
        item.subTotalDescuento = item.calcularImporteDescuento();
      }

      return item;
    });
  }

  eliminarItem(index: number): void {
    this.envio.itemsEnvio.splice(index, 1);
  }

  create(): void {
    this.envio.cliente = this.cliente;
    this.envio.usuario = this.usuario;
    this.envio.abono = this.abono;
    this.envio.saldoPendiente = this.saldoRestante;
    this.envio.totalEnvio = this.envio.calcularTotal();
    console.log(this.envio);

    this.envioService.create(this.envio).subscribe(response => {
      this.cliente = new Cliente();
      this.envio = new Envio();
      this.myBuscarTexto.nativeElement.value = '';
      this.router.navigate(['/envios/index']);
      swal.fire('Pedido Realizado', `Pedido No. ${response.envio.idEnvio} creado satisfactoriamente.`, 'success');
      this.myBuscarTexto.nativeElement.focus();

      // AQÍ VA EL CÓDIGO PARA GENERAR EL PDF
      this.print(response.envio);
      
    });
  }

  loadProducto(event): void {
    (document.getElementById ('button-x')).click(); // No se utilizó el nativeElement ya que no reconocia el botón para cerrar el modal
    this.buscarProductoPorID(event.idProducto);
    this.myCantidadRef.nativeElement.focus();
  }

  loadCliente(event): void {
    this.myBuscarTexto.nativeElement.value = event.nit;
    (document.getElementById('button-2x')).click(); // No se utilizó el nativeElement ya que no reconocia el botón para cerrar el modal
    this.buscarCliente();
  }

  calcularSaldo(event): void {
    if (this.abono) {
      this.saldoRestante = this.envio.calcularTotal() - this.abono;
    } else {
      this.saldoRestante = 0.00
    }
  }

  print(envio: Envio): void {
    this.envioService.getEnvioPdf(envio.idEnvio).subscribe(response => {
      const url = window.URL.createObjectURL(response.data);
      const a = document.createElement('a');
      document.body.appendChild(a);
      a.setAttribute('style', 'display: none');
      a.setAttribute('target', 'blank');
      a.href = url;

      window.open(a.toString(), '_blank');
      window.URL.revokeObjectURL(url);
      a.remove();
    },
      error => {
        console.log(error);
    });
  }
}
