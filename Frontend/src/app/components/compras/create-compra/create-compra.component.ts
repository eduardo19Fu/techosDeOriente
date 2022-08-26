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
import { Producto } from '../../../models/producto';
import { ProductoService } from '../../../services/producto.service';
import { DetalleCompra } from '../../../models/detalle-compra';
import { TipoProductoService } from '../../../services/tipo-producto.service';
import { MarcaProductoService } from '../../../services/marca-producto.service';
import { TipoProducto } from '../../../models/tipo-producto';
import { MarcaProducto } from '../../../models/marca-producto';

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
  producto: Producto;

  tiposComprobante: TipoComprobante[];
  proveedores: Proveedor[];
  productos: Producto[];
  tiposProducto: TipoProducto[];
  marcasProducto: MarcaProducto[];


  constructor(
    private router: Router,
    private compraService: CompraService,
    private proveedorService: ProveedorService,
    private productoService: ProductoService,
    private tipoProductoService: TipoProductoService,
    private marcaProductoService: MarcaProductoService,
    private usuarioService: UsuarioService,
    private authService: AuthService
  ) {
    this.compra = new Compra();
    this.title = 'Nueva Compra';
    this.producto = new Producto();
  }

  ngOnInit(): void {
    this.loadTiposComprobante();
    this.loadProveedores();
    this.cargarMarcas();
    this.cargarTipos();
  }

  /**
   * Método create que ejecuta la petición para registrar la nueva compra en la Base de Datos del lado
   * del Backend.
   * 
   */
  create(): void {
    let bandera = false;
    this.usuarioService.getUsuario(this.authService.usuario.idUsuario).subscribe(
      response => {
        this.compra.usuario = response;

        if (this.compra.usuario) {
          this.compra.items.forEach(item => {
            if (item.producto.nombre.length === 0) {
              Swal.fire('');
              bandera = false;
              return;
            } else {
              bandera = !bandera;
              return;
            }
          });

          if (bandera) {

            this.compraService.create(this.compra).subscribe(
              response => {
                this.router.navigate(['/compras/index']);
                Swal.fire(response.mensaje, `La compra: ${response.compra.noComprobante} fue guardada con éxito.`, 'success');
              }
            );

          }

        }
      }
    );
  }

  /** 
   * Función que se encarga de llevar a cabo la busqueda del producto a partir del codigo ingresado por el usuario.
   * 
   * */
  buscarProducto(): void {
    const codigo = ((document.getElementById('codigo') as HTMLInputElement)).value;

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

  /** 
   * Agregar un nuevo Item de de tipo Producto a cada DetalleCompra una vez se activa el evento requerido.
   * 
  */
  agregarLinea(): void {
    if (this.producto) { // comprueba que el producto exista
      const item = new DetalleCompra();

      item.cantidad = +((document.getElementById('cantidad') as HTMLInputElement)).value; // valor obtenido del formulario de cantidad

      if (item.cantidad && item.cantidad !== 0) {

        (document.getElementById('cantidad') as HTMLInputElement).value = '';

        if (!this.producto.codProducto || this.producto.codProducto.length === 0) {
          this.producto.codProducto = this.producto.generarCodigo();
        }

        this.producto.precioVenta = +(document.getElementById('precio-venta') as HTMLInputElement).value;

        item.producto = this.producto;
        item.subTotal = item.calcularSubTotal();
        item.precioUnitario = item.producto.precioCompra;

        if (item.producto.precioCompra && item.producto.porcentajeGanancia) {

          if (item.producto.nombre) {
            this.compra.items.push(item);
            this.producto = new Producto();
            console.log(item.producto);
  
            (document.getElementById('cantidad') as HTMLInputElement).value = '';
          } else {
            Swal.fire('Advertencia', 'No se puede agregar un nuevo producto sin un nombre válido.', 'warning');
          }

        } else {
          Swal.fire('Adevertencia', 'No se puede agregar una linea nueva sin los valores de precio compra y porcentaje ganancia.', 'warning');
        }

      } else if (item.cantidad === 0) {
        Swal.fire('Cantidad Erronéa', 'La cantidad a agregar debe ser mayor a 0.', 'warning');
      } else if (!item.cantidad) {
        Swal.fire('Valor Inválido', 'La cantidad no puede estar vacía.  Ingrese un valor válido.', 'warning');
      }
    }
  }

  eliminarItem(index: number): void {
    this.compra.items.splice(index, 1);
  }

  /**
   * Función que obtiene el listado de tipos de comprobante disponible en la base de datos.
   * 
   */
  loadTiposComprobante(): void {
    this.compraService.getTiposComprobante().subscribe(
      tiposComprobante => {
        this.tiposComprobante = tiposComprobante;
      }
    );
  }

  /**
   * Función que devuelve el listado de proveedores registrado en la base de datos.
   */
  loadProveedores(): void {
    this.proveedorService.getProveedores().subscribe(
      proveedores => this.proveedores = proveedores
    );
  }

  cargarMarcas(): void {
    this.marcaProductoService.getMarcas().subscribe(
      marcasProducto => this.marcasProducto = marcasProducto
    );
  }

  /**
   * Función que realiza la petición de los tipos de Producto para el registro de uno nuevo en caso de no existir.
   */
  cargarTipos(): void {
    this.tipoProductoService.getTiposProducto().subscribe(
      tiposProducto => this.tiposProducto = tiposProducto
    );
  }

  /** 
   * Función que se encarga de recibir el producto elegido desde la ventana de opciones por el usuario 
   * @param event Recibe el evento desde la ventana de mostrar productos
   * */
  loadProducto(event): void {
    (document.getElementById('codigo') as HTMLInputElement).value = event.codProducto;
    (document.getElementById('button-x')).click();
    this.buscarProducto();
    (document.getElementById('cantidad') as HTMLInputElement).focus();
  }

  /**
   * Método que muestra en el field de campo para precio de venta el calculo devuelto.
   * 
   */
  mostrarPrecioVenta(): void {
    (document.getElementById('precio-venta') as HTMLInputElement).value
      = this.producto.calcularPrecioVenta(this.producto.precioCompra, this.producto.porcentajeGanancia).toString();
  }
}
