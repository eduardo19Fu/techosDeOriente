import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { MarcaProductoService } from 'src/app/services/marca-producto.service';
import { ProductoService } from 'src/app/services/producto.service';
import { TipoProductoService } from 'src/app/services/tipo-producto.service';

import { MarcaProducto } from 'src/app/models/marca-producto';
import { Producto } from 'src/app/models/producto';
import { TipoProducto } from 'src/app/models/tipo-producto';

import swal from 'sweetalert2';
import { Proveedor } from 'src/app/models/proveedor';
import { ProveedorService } from 'src/app/services/proveedores/proveedor.service';

@Component({
  selector: 'app-create-producto',
  templateUrl: './create-producto.component.html',
  styles: [
  ]
})
export class CreateProductoComponent implements OnInit {

  title: string;
  producto: Producto;

  tipos: TipoProducto[];
  marcas: MarcaProducto[];
  proveedores: Proveedor[];

  constructor(
    private serviceMarca: MarcaProductoService,
    private serviceTipo: TipoProductoService,
    private serviceProducto: ProductoService,
    private serviceProveedor: ProveedorService,
    private router: Router,
    private activatedRoute: ActivatedRoute
  ) {
    this.title = 'Registro de Productos';
    this.producto = new Producto();
  }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe(params => {
      const id = params['id'];

      if (id) {
        this.serviceProducto.getProducto(id).subscribe(
          producto => {
            this.producto = new Producto();
            this.producto = producto
          }
        );
      }
    });
    this.cargarMarcas();
    this.cargarTipos();
    this.cargarProveedores();
  }

  cargarProducto(): void {
    this.activatedRoute.params.subscribe(params => {
      const id = params['id'];

      if (id) {
        this.serviceProducto.getProducto(id).subscribe(
          producto => this.producto = producto
        );
      }
    });
  }

  cargarMarcas(): void {
    this.serviceMarca.getMarcas().subscribe(marcas => this.marcas = marcas);
  }

  cargarTipos(): void {
    this.serviceTipo.getTiposProducto().subscribe(tipos => this.tipos = tipos);
  }

  cargarProveedores(): void {
    this.serviceProveedor.getProveedores().subscribe(proveedores => this.proveedores = proveedores);
  }

  create(): void {
    this.producto.precioSugerido = Number.parseFloat((document.getElementById('precio-sugerido') as HTMLInputElement).value);
    this.serviceProducto.create(this.producto).subscribe(
      response => {
        this.router.navigate(['/productos/index']);
        swal.fire('Producto Guardado', `${response.mensaje}: ${response.producto.nombre}`, 'success');
      }
    );
  }

  update(): void {
    this.producto.precioVenta = Number.parseFloat((document.getElementById('precio-venta') as HTMLInputElement).value);
    this.serviceProducto.update(this.producto).subscribe(
      response => {
        this.router.navigate(['/productos/index']);
        swal.fire('Producto Actualizado', `${response.mensaje}: ${response.producto.nombre}`, 'success');
      }
    );
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
    return o1 === null || o2 === null || o1 === undefined || o2 === undefined ? false : o1.idProveedor === o2.idProveedor;
  }

  calcularPorcentajeGanancia(): void {
    const pcompra = ((document.getElementById('precio-compra') as HTMLInputElement).value);
    const pventa = (document.getElementById('precio-venta') as HTMLInputElement).value;
    let porcentaje = 0;

    if (!pcompra || !pventa) {
      console.log('valores incorrectos');
    } else {
      porcentaje = ((Number.parseFloat(pventa) - Number.parseFloat(pcompra)) / Number.parseFloat(pcompra)) * 100;
    }

    (document.getElementById('porcentaje-ganancia') as HTMLInputElement).value = porcentaje.toString();
  }

  mostrarPrecioSugerido(): void {
    // this.producto = new Producto();
    const pPorcentaje = +((document.getElementById('porcentaje-ganancia') as HTMLInputElement).value);
    const pCompra = +((document.getElementById('precio-compra') as HTMLInputElement).value);
    let precioSugerido = 0;

    if (!pPorcentaje || !pCompra) {
      console.log('Valores Incorrectos');
    } else {
      // precioSugerido = this.producto.calcularPrecioSugerido(+pcompra, +porcentajeGanancia);
      precioSugerido = ((pCompra) * pPorcentaje / 100) +  pCompra;
      (document.getElementById('precio-sugerido') as HTMLInputElement).value = precioSugerido.toString();
    }

  }

}
