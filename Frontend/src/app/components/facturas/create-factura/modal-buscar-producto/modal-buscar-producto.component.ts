import { Component, OnInit, Output, EventEmitter, ElementRef, AfterViewInit, ChangeDetectorRef, OnDestroy } from '@angular/core';
import { ProductoService } from '../../../../services/producto.service';
import { Producto } from '../../../../models/producto';
import { JqueryConfigs } from '../../../../utils/jquery/jquery-utils';

declare var $: any;

@Component({
  selector: 'app-modal-buscar-producto',
  templateUrl: './modal-buscar-producto.component.html',
  styles: [
  ]
})
export class ModalBuscarProductoComponent implements OnInit {

  @Output() producto = new EventEmitter<Producto>();

  title: string;
  loading: boolean = false;
  productos: Producto[] = [];

  jqueryConfigs: JqueryConfigs;

  constructor(
    private productoService: ProductoService,
    private elementRef: ElementRef,
    private cdr: ChangeDetectorRef
  ) {
    this.title = 'Búsqueda de Productos';
    this.jqueryConfigs = new JqueryConfigs();  
  }

  ngOnInit(): void {
    this.loadProductos();
  }

  loadProductos(): void{
    this.productoService.getProductosActivosSP().subscribe(
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
