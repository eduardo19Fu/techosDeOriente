import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { ProductoService } from '../../../../services/producto.service';
import { Producto } from '../../../../models/producto';
import { JqueryConfigs } from '../../../../utils/jquery/jquery-utils';

@Component({
  selector: 'app-modal-buscar-producto',
  templateUrl: './modal-buscar-producto.component.html',
  styles: [
  ]
})
export class ModalBuscarProductoComponent implements OnInit {

  @Output() producto = new EventEmitter<Producto>();

  title: string;
  productos: Producto[];

  jqueryConfigs: JqueryConfigs = new JqueryConfigs();

  constructor(
    private productoService: ProductoService
  ) {
    this.title = 'Búsqueda de Productos';
  }

  ngOnInit(): void {
    this.loadProductos();
  }

  loadProductos(): void{
    this.productoService.getProductosActivos().subscribe(
      productos => {
        this.productos = productos;
        this.jqueryConfigs.configDataTableModal("productos");
      }
    );
  }

  chooseProducto(producto: Producto): void{
    this.producto.emit(producto);
  }

}
