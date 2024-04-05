import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { DetallePedido } from '../../../models/detalle-pedido';
import { MarcaProducto } from '../../../models/marca-producto';
import { Pedido } from '../../../models/pedido';
import { Producto } from '../../../models/producto';
import { Proveedor } from '../../../models/proveedor';
import { TipoProducto } from '../../../models/tipo-producto';

import { AuthService } from '../../../services/auth.service';
import { MarcaProductoService } from '../../../services/marca-producto.service';
import { PedidoService } from '../../../services/pedidos/pedido.service';
import { ProductoService } from '../../../services/producto.service';
import { ProveedorService } from '../../../services/proveedores/proveedor.service';
import { TipoProductoService } from '../../../services/tipo-producto.service';
import { UsuarioService } from '../../../services/usuarios/usuario.service';

import Swal from 'sweetalert2';

@Component({
  selector: 'app-create-pedido',
  templateUrl: './create-pedido.component.html',
  styleUrls: ['./create-pedido.component.css']
})
export class CreatePedidoComponent implements OnInit {

  title: string;
  tmpvalorCompra: number;

  pedido: Pedido;
  producto: Producto;
  proveedores: Proveedor[];
  marcasProducto: MarcaProducto[];
  tiposProducto: TipoProducto[];

  constructor(
    public authService: AuthService,
    private pedidoService: PedidoService,
    private productoService: ProductoService,
    private proveedorService: ProveedorService,
    private marcasService: MarcaProductoService,
    private tiposService: TipoProductoService,
    private usuarioService: UsuarioService,
    private router: Router
  ) {
    this.title = "Crear Pedido";
    this.pedido = new Pedido();
    this.producto = new Producto();
    this.proveedores = [];
  }

  ngOnInit(): void {
    this.loadProveedores();
    this.loadMarcas();
    this.loadTipos();
  }

  loadProducto(event): void {
    (document.getElementById('codigo') as HTMLInputElement).value = event.codProducto;
    (document.getElementById('button-x')).click();
    this.buscarProductoPorID(event.idProducto);
    (document.getElementById('cantidad') as HTMLInputElement).focus();
  }

  buscarProductoPorID(id: number): void {
    if(id) {
      this.productoService.getProducto(id).subscribe(
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

            this.tmpvalorCompra = this.producto.precioCompra;
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

            this.tmpvalorCompra = this.producto.precioCompra;
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

  loadProveedores(): void {
    this.proveedorService.getProveedores().subscribe(
      response => {
        this.proveedores = response;
      });
  }

  loadMarcas(): void {
    this.marcasService.getMarcas().subscribe(
      marcas => {
        this.marcasProducto = marcas;
      }
    );
  }

  loadTipos(): void {
    this.tiposService.getTiposProducto().subscribe(
      tipos => {
        this.tiposProducto = tipos;
      }
    );
  }

  /**
   * Método create que ejecuta la petición para registrar la nueva compra en la Base de Datos del lado
   * del Backend.
   * 
   */
  create(): void {
    this.usuarioService.getUsuario(this.authService.usuario.idUsuario).subscribe(
      response => {
        this.pedido.usuario = response;

        if (this.pedido.usuario) {
          this.pedidoService.create(this.pedido).subscribe(
            response => {
              this.router.navigate(['/pedidos/index']);
              Swal.fire(response.mensaje, `La compra: ${response.pedido.idPedido} fue guardada con éxito.`, 'success');
              
              this.generarPedido(response.pedido);
            }
          );
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
      const item = new DetallePedido();
    
      item.cantidad = +((document.getElementById('cantidad') as HTMLInputElement)).value; // valor obtenido del formulario de cantidad

      if (item.cantidad && item.cantidad !== 0) {

        (document.getElementById('cantidad') as HTMLInputElement).value = '';

        if (!this.producto.codProducto || this.producto.codProducto.length === 0) {
          this.producto.codProducto = 'GENERADO-' + this.producto.generarCodigo();
        }

        if (!this.producto.serie || this.producto.serie.length === 0) {
          this.producto.serie = 'GENERADO-SERIE-' + this.producto.generarCodigo();
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
              this.pedido.itemsPedido.push(item);
              this.producto = new Producto();

              // Asigna el proveedor de la compra a los productos asignados
              if(!this.pedido.proveedor) {
                Swal.fire('Advertencia', 'Debe elegirse un proveedor para llevar a cabo el registro de la compra', 'warning');
                return;
              } else {
                this.pedido.itemsPedido.forEach(item => {
                  item.producto.proveedor = this.pedido.proveedor;
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

  /** GENERAR REPORTE CON EL PEDIDO RECIÉN CREADO **/
  generarPedido(pedido: Pedido): void {
    this.pedidoService.getPedidoPDF(pedido.idPedido).subscribe(response => {
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

  eliminarItem(index: number): void {
    this.pedido.itemsPedido.splice(index, 1);
  }

  /**
   * Método que muestra en el field de campo para precio de venta el calculo devuelto.
   * 
   */
  mostrarPrecioSugerido(): void {
    (document.getElementById('precio-sugerido') as HTMLInputElement).value
      = this.producto.calcularPrecioSugerido(this.producto.precioCompra, this.producto.porcentajeGanancia).toString();
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
