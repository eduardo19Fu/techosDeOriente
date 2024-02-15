import { Component, ElementRef, OnInit, Renderer2, ViewChild } from '@angular/core';

import { UsuarioAuxiliar } from 'src/app/models/auxiliar/usuario-auxiliar';
import { Cliente } from 'src/app/models/cliente';
import { Correlativo } from 'src/app/models/correlativo';
import { DetalleFactura } from 'src/app/models/detalle-factura';
import { Factura } from 'src/app/models/factura';
import { Producto } from 'src/app/models/producto';

import { AuthService } from 'src/app/services/auth.service';
import { ClienteService } from 'src/app/services/cliente.service';
import { CorrelativoService } from 'src/app/services/correlativos/correlativo.service';
import { ClienteCreateService } from 'src/app/services/facturas/cliente-create.service';
import { FacturaService } from 'src/app/services/facturas/factura.service';
import { ProductoService } from 'src/app/services/producto.service';
import { UsuarioService } from 'src/app/services/usuarios/usuario.service';

import swal from 'sweetalert2';
import { ModalCambioService } from '../../../services/facturas/modal-cambio.service';
import { EnvioService } from '../../../services/envios/envio.service';
import { Envio } from '../../../models/envio';
import { ActivatedRoute } from '@angular/router';
import { DetalleEnvio } from '../../../models/detalle-envio';
import { TipoFactura } from '../../../models/tipo-factura';

@Component({
  selector: 'app-create-factura',
  templateUrl: './create-factura.component.html',
  styleUrls: ['./create-factura.component.css']
})
export class CreateFacturaComponent implements OnInit {

  @ViewChild('mybuscar') myBuscarTexto: ElementRef;
  @ViewChild('myEfectivo') myEfectivoRef: ElementRef;

  title: string;
  nitIngresado: string;
  pagar = false;

  producto: Producto;
  cliente: Cliente;
  usuario: UsuarioAuxiliar;
  factura: Factura;
  correlativo: Correlativo;
  envio: Envio;

  efectivo: number;
  cambio = 0.00;

  constructor(
    private facturaService: FacturaService,
    private productoService: ProductoService,
    private clienteService: ClienteService,
    private usuarioService: UsuarioService,
    private clienteCreateService: ClienteCreateService,
    private correlativoService: CorrelativoService,
    private envioService: EnvioService,
    private activatedRoute: ActivatedRoute,
    public authService: AuthService
  ) {
    this.title = 'Crear Factura';
    this.cliente = new Cliente();
    this.usuario = new UsuarioAuxiliar();
    this.factura = new Factura();
    this.correlativo = new Correlativo();
    this.producto = new Producto();
  }

  ngOnInit(): void {
    this.loadUsuario();
    this.loadEnvio();
  }

  loadUsuario(): void {
    this.usuarioService.getUsuario(this.authService.usuario.idUsuario).subscribe(
      usuario => {
        this.usuario = usuario;
        this.cargarCorrelativo();
      }
    );
  }

  buscarCliente(): void {
    // const nit = ((document.getElementById('buscar') as HTMLInputElement)).value;
    const nit = this.myBuscarTexto.nativeElement.value;

    if (nit) {
      this.clienteService.getClienteByNit(nit).subscribe(
        cliente => {
          this.cliente = cliente;
          (document.getElementById('serie')).focus();
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

  cargarCorrelativo(): void {
    if (this.usuario) {
      this.correlativoService.getCorrelativoPorUsuario(this.usuario.idUsuario).subscribe(
        correlativo => {
          this.correlativo = correlativo;
        },
        error => {
          swal.fire('Error al cargar correlativo', error.error.mensaje, 'error');
        }
      );
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
            swal.fire(`Error: ${error.status}`, 'Petición no se puede llevar a cabo.', 'error');
          }

          if (error.status === 404) {
            swal.fire(`Error: ${error.status}`, error.error.mensaje, 'error');
          }
        }
      );
    }
  }

  buscarProductoPorId(id: number): void {
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

  buscarProductoPorCodigo(codigo: string): void {
    this.productoService.getProductoByCode(codigo).subscribe(
      producto => {
        console.log(producto);
        this.producto = producto;
        console.log(this.producto);
        (document.getElementById('cantidad') as HTMLInputElement).focus();
      }, error => {
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
      swal.fire('Ha ocurrido un Problema', 'Por favor, elija un cliente antes de llevar a cabo la venta.', 'error');
    } else {
      if (this.producto) { // comprueba que el producto exista
        const item = new DetalleFactura();

        item.cantidad = +((document.getElementById('cantidad') as HTMLInputElement)).value; // valor obtenido del formulario de cantidad
        item.descuento = 0; // valor obtenido del input de descuento

        if (item.cantidad > this.producto.stock) {
          swal.fire('Stock Insuficiente', 'No existen las suficientes existencias de este producto.', 'warning');
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
              this.factura.itemsFactura.push(item);
              this.producto = new Producto();

              (document.getElementById('cantidad') as HTMLInputElement).value = '';
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

    this.factura.itemsFactura = this.factura.itemsFactura.map((item: DetalleFactura) => {
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

    this.factura.itemsFactura = this.factura.itemsFactura.map((item: DetalleFactura) => {
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
    this.factura.itemsFactura.forEach((item: DetalleFactura) => {
      if (id === item.producto.idProducto) {
        existe = true;
      }
    });
    return existe;
  }

  incrementaCantidad(idProducto: number, cantidad: number): void {
    this.factura.itemsFactura = this.factura.itemsFactura.map((item: DetalleFactura) => {
      if (idProducto === item.producto.idProducto) {
        item.cantidad = item.cantidad + cantidad;
        item.subTotal = item.calcularImporte();
        item.subTotalDescuento = item.calcularImporteDescuento();
      }

      return item;
    });
  }

  eliminarItem(index: number): void {
    this.factura.itemsFactura.splice(index, 1);
  }

  validarTipoFactura(): TipoFactura {
    let tipoFactura: TipoFactura = new TipoFactura();

    // Retornamos el valor asignado a tipo factura determinando si existe o no el envío pasado por url.
    return (!this.envio ? tipoFactura = {idTipoFactura: 1, tipoFactura: "NORMAL"} : tipoFactura = {idTipoFactura: 3, tipoFactura: "PAGO_ENVIO"});

  }

  createFactura(): void {
    this.factura.noFactura = this.correlativo.correlativoActual;
    this.factura.serie = this.correlativo.serie;
    this.factura.cliente = this.cliente;
    this.factura.usuario = this.usuario;
    this.factura.envio = this.envio;
    this.factura.total = this.factura.calcularTotal();
    this.factura.tipoFactura = this.validarTipoFactura();

    this.facturaService.create(this.factura).subscribe(
      response => {
        this.cliente = new Cliente();
        this.factura = new Factura();
        this.cargarCorrelativo();
        this.myBuscarTexto.nativeElement.value = '';
        swal.fire('Venta Realizada', `Factura No. ${response.factura.noFactura} creada con éxito!`, 'success');
        this.myBuscarTexto.nativeElement.focus();
        this.cambio = 0;
        this.myEfectivoRef.nativeElement.value = '';

        this.facturaService.getBillPDF(response.factura.idFactura).subscribe(res => {
          const url = window.URL.createObjectURL(res.data);
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
            swal.fire(`Error al crear factura para imprimir.`, error.message, 'error');
          });
        
        /*
        ------- Código para abrir una url en caso de tener activado FEL ---------
        const url = 'https://report.feel.com.gt/ingfacereport/ingfacereport_documento?uuid=' + response.factura.certificacionSat;

        const a = document.createElement('a');
        window.open(url, '_blank').focus(); */

      }
    );
  }

  calcularCambio(event): void {
    if (this.efectivo) {
      this.cambio = this.efectivo - this.factura.calcularTotal();
    } else {
      this.cambio = 0.00;
    }
  }

  loadProducto(event): void {
    console.log(event);
    (document.getElementById('serie') as HTMLInputElement).value = event.serie;
    this.buscarProductoPorId(event.idProducto);
    (document.getElementById('button-x')).click();
    (document.getElementById('cantidad') as HTMLInputElement).focus();
  }

  loadCliente(event): void {
    (document.getElementById('buscar') as HTMLInputElement).value = event.nit;
    (document.getElementById('button-2x')).click();
    this.buscarCliente();
  }

  /**
   * Función que evalúa si el parámetro que permite la carga del envío viene incluido en la ruta.
   * 
   */

   loadEnvio(): void {
    this.activatedRoute.params.subscribe(param => {
      const idenvio = param.envio;

      if (idenvio) {
        this.envioService.getEnvio(idenvio).subscribe(
          envio => {
            this.envio = envio;
            if (this.envio) {
              this.loadEnvioToBilling(envio);
            }
          }
        );
      }
    }, error => {
      console.log(error);
    });
  }

  /**
   * Carga el envio recibido por ruta para poderse llevar a cabo la facturación.
   * @param envio Parámetro que sirve para realizar la busqueda del envío deseado por medio de su ID.
   */

  loadEnvioToBilling(envio: Envio): void {
    let itemFactura: DetalleFactura;
    this.cliente = envio.cliente;

    envio.itemsEnvio.forEach((env: DetalleEnvio) => {
      itemFactura = new DetalleFactura();
      
      itemFactura.producto = env.producto;
      itemFactura.cantidad = env.cantidad;
      itemFactura.subTotal = env.subTotal;
      itemFactura.descuento = env.descuento;
      itemFactura.subTotalDescuento = env.subTotalDescuento;
      
      this.factura.itemsFactura.push(itemFactura);
    });
  }

}
