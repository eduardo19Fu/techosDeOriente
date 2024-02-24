import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { Producto } from '../../../models/producto';
import { Cliente } from '../../../models/cliente';
import { UsuarioAuxiliar } from '../../../models/auxiliar/usuario-auxiliar';
import { Cotizacion } from '../../../models/cotizacion';
import { AuthService } from '../../../services/auth.service';
import { CotizacionService } from '../../../services/cotizaciones/cotizacion.service';
import { ProductoService } from '../../../services/producto.service';
import { ClienteService } from '../../../services/cliente.service';
import { UsuarioService } from '../../../services/usuarios/usuario.service';
import { ClienteCreateService } from '../../../services/facturas/cliente-create.service';
import { DetalleCotizacion } from '../../../models/detalle-cotizacion';

import Swal from 'sweetalert2';

@Component({
  selector: 'app-create-cotizacion',
  templateUrl: './create-cotizacion.component.html',
  styleUrls: ['./create-cotizacion.component.css']
})
export class CreateCotizacionComponent implements OnInit {

  @ViewChild('mybuscar') myBuscarTexto: ElementRef;

  title: string;
  nitIngresado: string;
  pagar = false;

  producto: Producto;
  cliente: Cliente;
  usuario: UsuarioAuxiliar;
  cotizacion: Cotizacion;

  constructor(
    public authService: AuthService,
    private cotizacionService: CotizacionService,
    private productoService: ProductoService,
    private clienteService: ClienteService,
    private clienteCreateService: ClienteCreateService,
    private usuarioService: UsuarioService,
    private activatedRoute: ActivatedRoute
  ) 
  {
    this.title = 'Registrar Nueva Cotización';
    this.usuario = new UsuarioAuxiliar();
    this.cliente = new Cliente();
    this.producto = new Producto();
    this.cotizacion = new Cotizacion();
  }

  ngOnInit(): void {
    this.loadUsuario();
  }

  loadUsuario(): void {
    this.usuarioService.getUsuario(this.authService.usuario.idUsuario).subscribe(
      usuario => {
        this.usuario = usuario;
      }
    );
  }

  cargarCliente(event): void {
    this.cliente = event;
  }

  buscarCliente(): void {
    const nit = this.myBuscarTexto.nativeElement.value;

    if (nit) {
      this.clienteService.getClienteByNit(nit).subscribe(
        cliente => {
          this.cliente = cliente;
          (document.getElementById('serie')).focus();
        },
        error => {
          if (error.status === 400) {
            Swal.fire(`Error: ${error.status}`, 'Petición Equivocada', 'error');
          }
          if (error.status === 404) {
            this.nitIngresado = nit;
            this.clienteCreateService.abrirModal();
          }
        }
      );
    } else {
      Swal.fire('NIT Vacío', 'Ingrese un valor valido para realizar la búsqueda.', 'warning');
    }
  }

  buscarProducto(): void {
    const serie = ((document.getElementById('serie') as HTMLInputElement)).value;

    if (serie) {
      this.productoService.getProductoBySerie(serie).subscribe(
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
    }
  }

  buscarProductoPorID(id: number): void {
    this.productoService.getProducto(id).subscribe(
      producto => {
        this.producto = producto;(document.getElementById('cantidad') as HTMLInputElement).focus();
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
  }

  buscarProductoPorCodigo(codigo: string): void {
    this.productoService.getProductoByCode(codigo).subscribe(
      producto => {
        this.producto = producto;
        (document.getElementById('cantidad') as HTMLInputElement).focus();
      }, error => {
        if (error.status === 400) {
          Swal.fire(`Error: ${error.status}`, 'Petición no se puede llevar a cabo.', 'error');
        }

        if (error.status === 404) {
          Swal.fire(`Error: ${error.status}`, error.error.mensaje, 'error');
        }
      }
    );
  }

  agregarLinea(): void {
    if (!this.cliente) { // Comprueba que el cliente exista
      Swal.fire('Ha ocurrido un Problema', 'Por favor, elija un cliente antes de llevar a cabo la venta.', 'error');
    } else {
      if (this.producto) { // comprueba que el producto exista
        const item = new DetalleCotizacion();

        item.cantidad = +((document.getElementById('cantidad') as HTMLInputElement)).value; // valor obtenido del formulario de cantidad
        item.descuento = 0; // valor obtenido del input de descuento

        if (item.cantidad > this.producto.stock) {
          Swal.fire('Stock Insuficiente', 'No existen las suficientes existencias de este producto.', 'warning');
          return;
        } else {
          if (item.cantidad && item.cantidad !== 0) {
            if (this.existeItem(this.producto.idProducto)) {
              this.incrementaCantidad(this.producto.idProducto, item.cantidad);
              this.producto = new Producto();
              (document.getElementById('cantidad') as HTMLInputElement).value = '';
            } else {
              item.producto = this.producto;
              item.subTotalDescuento = item.calcularImporte();
              item.subTotal = item.calcularImporte();
              this.cotizacion.itemsProforma.push(item);
              this.producto = new Producto();

              (document.getElementById('cantidad') as HTMLInputElement).value = '';
            }

          } else if (item.cantidad === 0) {
            Swal.fire('Cantidad Erronéa', 'La cantidad a agregar debe ser mayor a 0.', 'warning');
          } else if (!item.cantidad) {
            Swal.fire('Valor Inválido', 'La cantidad no puede estar vacía.  Ingrese un valor válido.', 'warning');
          }
        }
      }
    }
  }

  actualizarCantidad(idProducto: number, event: any): void {
    const cantidad = event.target.value as number;

    this.cotizacion.itemsProforma = this.cotizacion.itemsProforma.map((item: DetalleCotizacion) => {
      if (idProducto === item.producto.idProducto) {
        if (cantidad > item.producto.stock) {
          Swal.fire('Stock Insuficiente', 'No existen las suficientes existencias de este producto.', 'warning');
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

    this.cotizacion.itemsProforma = this.cotizacion.itemsProforma.map((item: DetalleCotizacion) => {
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
    this.cotizacion.itemsProforma.forEach((item: DetalleCotizacion) => {
      if (id === item.producto.idProducto) {
        existe = true;
      }
    });
    return existe;
  }

  incrementaCantidad(idProducto: number, cantidad: number): void {
    this.cotizacion.itemsProforma = this.cotizacion.itemsProforma.map((item: DetalleCotizacion) => {
      if (idProducto === item.producto.idProducto) {
        item.cantidad = item.cantidad + cantidad;
        item.subTotal = item.calcularImporte();
        item.subTotalDescuento = item.calcularImporteDescuento();
      }

      return item;
    });
  }

  eliminarItem(index: number): void {
    this.cotizacion.itemsProforma.splice(index, 1);
  }

  loadProducto(event): void {
      (document.getElementById('serie') as HTMLInputElement).value = event.serie;
      this.buscarProductoPorID(event.idProducto);
      (document.getElementById('button-x')).click();
      (document.getElementById('cantidad') as HTMLInputElement).focus();
  }

  loadCliente(event): void {
    (document.getElementById('buscar') as HTMLInputElement).value = event.nit;
    (document.getElementById('button-2x')).click();
    this.buscarCliente();
  }

  createCotizacion(): void {
    // this.cotizacion.noFactura = this.correlativo.correlativoActual;
    // this.cotizacion.serie = this.correlativo.serie;
    this.cotizacion.cliente = this.cliente;
    this.cotizacion.usuario = this.usuario;
    // this.cotizacion.envio = this.envio;
    this.cotizacion.total = this.cotizacion.calcularTotal();
    // this.cotizacion.tipoFactura = this.validarTipoFactura();

    this.cotizacionService.create(this.cotizacion).subscribe(
      response => {
        this.cliente = new Cliente();
        this.cotizacion = new Cotizacion();
        this.myBuscarTexto.nativeElement.value = '';
        Swal.fire('Cotización creada exitosamente', `Cotización No. ${response.proforma.idCotizacion} creada con éxito!`, 'success');
        this.myBuscarTexto.nativeElement.focus();

        this.generarCotizacionPDF(response.proforma);

      }
    );
  }

  generarCotizacionPDF(cotizacion: Cotizacion): void {
    this.cotizacionService.getCotizacionPDF(cotizacion.idCotizacion).subscribe(response => {
      const url = window.URL.createObjectURL(response.data);
      const a = document.createElement('a');
      document.body.appendChild(a);
      a.setAttribute('style', 'display: none');
      a.setAttribute('target', 'blank');
      a.href = url;
      /*
        opcion para pedir descarga de la respuesta obtenida
        a.download = response.filename;
      */
      window.open(a.toString(), '_blank');
      window.URL.revokeObjectURL(url);
      a.remove();
    },
      error => {
        Swal.fire(`Error al crear factura para imprimir.`, error.message, 'error');
      });
  }
}
