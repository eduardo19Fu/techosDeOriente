import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { Compra } from '../../../models/compra';
import { TipoComprobante } from '../../../models/tipo-comprobante';
import { UsuarioAuxiliar } from '../../../models/auxiliar/usuario-auxiliar';
import { Proveedor } from '../../../models/proveedor';
import { Producto } from '../../../models/producto';
import { DetalleCompra } from '../../../models/detalle-compra';
import { TipoProducto } from '../../../models/tipo-producto';
import { MarcaProducto } from '../../../models/marca-producto';

import { UsuarioService } from '../../../services/usuarios/usuario.service';
import { ProductoService } from '../../../services/producto.service';
import { TipoProductoService } from '../../../services/tipo-producto.service';
import { MarcaProductoService } from '../../../services/marca-producto.service';
import { ProveedorService } from '../../../services/proveedores/proveedor.service';
import { AuthService } from '../../../services/auth.service';
import { CompraService } from '../../../services/compras/compra.service';

import Swal from 'sweetalert2';

@Component({
  selector: 'app-create-compra',
  templateUrl: './create-compra.component.html',
  styles: [
  ]
})
export class CreateCompraComponent implements OnInit, OnDestroy {

  title: string;
  compraKey: string;
  nuevoTotal: number;

  compra: Compra;
  usuario: UsuarioAuxiliar;
  producto: Producto;

  tiposComprobante: TipoComprobante[];
  proveedores: Proveedor[];
  productos: Producto[];
  tiposProducto: TipoProducto[];
  marcasProducto: MarcaProducto[];

  guardarHabilitado: boolean = true;

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
    this.proveedores = [];
    this.nuevoTotal = 0;
  }

  ngOnInit(): void {
    this.loadTiposComprobante();
    this.loadProveedores();
    this.cargarMarcas();
    this.cargarTipos();
    
    // this.cargarCompra();
  }

  ngOnDestroy(): void {
    // this.compraService.almacenarCompra(this.compra);
  }

  // cargarCompra(): void {
  //   const compraGuardada = this.compraService.obtenerCompra();
  //   if(compraGuardada) {
  //     this.compra.fechaCompra = compraGuardada.fechaCompra;
  //     this.compra.totalCompra = compraGuardada.totalCompra;
  //     this.compra.noComprobante = compraGuardada.noComprobante;
  //     this.compra.proveedor = compraGuardada.proveedor;
  //     this.compra.tipoComprobante = compraGuardada.tipoComprobante;
  //     this.compra.items = compraGuardada.items;

  //     console.log(this.compra);
  //   }
  // }

  /**
   * Método create que ejecuta la petición para registrar la nueva compra en la Base de Datos del lado
   * del Backend.
   * 
   */
  create(): void {
    this.usuarioService.getUsuario(this.authService.usuario.idUsuario).subscribe(
      response => {
        this.compra.usuario = response;

        if (this.compra.usuario) {

          if (this.nuevoTotal && this.nuevoTotal > 0) {
            this.compra.totalCompra = this.nuevoTotal;
          }
          
          this.guardarHabilitado = false;
          this.compraService.create(this.compra).subscribe(
            response => {
              this.router.navigate(['/compras/index']);
              Swal.fire(response.mensaje, `La compra: ${response.compra.noComprobante} fue guardada con éxito.`, 'success');
              this.compraService.limpiarCompra();
            }
          );
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
          this.producto = new Producto();
          
          // Para evitar error al ejecutar funcion producto.calcularPrecioSugerido()
          this.producto.idProducto = producto.idProducto;
          this.producto.codProducto = producto.codProducto;
          this.producto.serie = producto.serie;
          this.producto.nombre = producto.nombre;
          this.producto.precioCompra = producto.precioCompra;
          this.producto.precioVenta = producto.precioVenta;
          this.producto.precioSugerido = producto.precioSugerido ? producto.precioSugerido : 0;
          this.producto.porcentajeGanancia = producto.porcentajeGanancia;
          this.producto.estado = producto.estado;
          this.producto.fechaIngreso = producto.fechaIngreso;
          this.producto.fechaRegistro = producto.fechaRegistro;
          this.producto.tipoProducto = producto.tipoProducto;
          this.producto.marcaProducto = producto.marcaProducto;

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

  buscarProductoPorId(id: number): void {
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
          this.producto.codProducto = 'GENERADO-' + this.producto.generarCodigo();
        }

        if (!this.producto.serie || this.producto.serie.length === 0) {
          this.producto.serie = 'GENERADO-SERIE-' + this.generarCodigo();
        }

        this.producto.precioSugerido = +(document.getElementById('precio-sugerido') as HTMLInputElement).value;
        this.producto.precioVenta = +(document.getElementById('precio-venta') as HTMLInputElement).value;

        if(this.producto.nombre && this.producto.nombre.length !== 0) {

        
          item.producto = this.producto;
          item.subTotal = item.calcularSubTotal();
          item.precioUnitario = item.producto.precioCompra;

          if (!item.producto.porcentajeGanancia || item.producto.porcentajeGanancia <= 0) {
            item.producto.porcentajeGanancia = item.producto.calcularPorcentajeGanancia(item.producto.precioCompra, this.producto.precioVenta);
          }

          if (item.producto.precioCompra && item.producto.porcentajeGanancia) {

            if (item.producto.nombre) {
              this.compra.items.push(item);
              // this.compraService.agregarItemCompra(item);
              this.producto = new Producto();

              // Asigna el proveedor de la compra a los productos asignados
              if(!this.compra.proveedor) {
                Swal.fire('Advertencia', 'Debe elegirse un proveedor para llevar a cabo el registro de la compra', 'warning');
                return;
              } else {
                this.compra.items.forEach(item => {
                  item.producto.proveedor = this.compra.proveedor;
                });
              }
    
              (document.getElementById('cantidad') as HTMLInputElement).value = '';
            } else {
              Swal.fire('Advertencia', 'No se puede agregar un nuevo producto sin un nombre válido.', 'warning');
            }

          } else {
            Swal.fire('Adevertencia', 'No se puede agregar una linea nueva sin los valores de precio compra y porcentaje ganancia.', 'warning');
          }
        } else {
          Swal.fire('Nombre Vacío', 'No se puede agregar un producto sin nombre a la lista de productos comprados.', 'warning');
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
    this.buscarProductoPorId(event.idProducto);
    // this.buscarProducto();
    (document.getElementById('cantidad') as HTMLInputElement).focus();
  }


  calcularTotalConNuevosValores(event): void {
    this.compra.totalFlete = +((document.getElementById("total-flete")) as HTMLInputElement).value;
    this.compra.totalDescuento = +((document.getElementById("total-descuento")) as HTMLInputElement).value;
    this.nuevoTotal = (this.compra.totalCompra - this.compra.totalDescuento) + this.compra.totalFlete;
  }

  /**
   * Método que muestra en el field de campo para precio de venta el calculo devuelto.
   * 
   */
  mostrarPrecioSugerido(): void {
    // (document.getElementById('precio-sugerido') as HTMLInputElement).value
    //   = this.producto.calcularPrecioSugerido(this.producto.precioCompra, this.producto.porcentajeGanancia).toString();
    const pPorcentaje = +((document.getElementById('porcentaje-ganancia') as HTMLInputElement).value);
    const pCompra = +((document.getElementById('precio-compra') as HTMLInputElement).value);
    let precioSugerido = 0;

    if (!pPorcentaje || !pCompra) {
      console.log('Valores Incorrectos.');
    } else {
      // precioSugerido = this.producto.calcularPrecioSugerido(+pcompra, +porcentajeGanancia);
      precioSugerido = ((pCompra) * pPorcentaje / 100) +  pCompra;
      (document.getElementById('precio-sugerido') as HTMLInputElement).value = precioSugerido.toString();
    }
  }

  /**
     * Método que genera un codigo aleatorio para un producto que no tenga código de barras predefinido.
     * @returns código
     */
  generarCodigo(): string {
    var rand: number;

    rand = Math.floor(Math.random() * 100000000) + 1; // DEVUELVE UN VALOR ALEATORIO ENTRE 1 Y 1000000
    return (rand.toString());
}

  // Comparar para reemplazar el valor en el select del formulario en caso de existir
  compararMarca(o1: MarcaProducto, o2: MarcaProducto): boolean {
    if (o1 === undefined && o2 === undefined) {
      return true;
    }
    return o1 === null || o2 === null || o1 === undefined || o2 === undefined ? false : o1.idMarcaProducto === o2.idMarcaProducto;
  }

  compararTipo(o1: TipoProducto, o2: TipoProducto): boolean {
    if (o1 === undefined && o2 === undefined) {
      return true;
    }
    return o1 == null || o2 == null || o1 === undefined || o2 === undefined ? false : o1.idTipoProducto === o2.idTipoProducto;
  }

  compararProveedor(o1: Proveedor, o2: Proveedor): boolean {
    if (o1 === undefined && o2 === undefined) {
      return true;
    }
    return o1 == null || o2 == null || o1 === undefined || o2 === undefined ? false : o1.idProveedor === o2.idProveedor;
  }
}
