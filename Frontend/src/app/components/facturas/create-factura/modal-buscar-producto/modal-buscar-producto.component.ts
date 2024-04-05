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
export class ModalBuscarProductoComponent implements OnInit, AfterViewInit, OnDestroy {

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
    // this.loadProductos();
  }

  ngOnDestroy(): void {
    // Desuscribirse del evento shown.bs.modal para evitar memory leaks
    $(this.elementRef.nativeElement).find('#modal-buscar-producto').off('shown.bs.modal');
    $(this.elementRef.nativeElement).find('#modal-buscar-producto').off('hidden.bs.modal');
  }

  ngAfterViewInit(): void {
    /** Carga los productos al disparar el modal */
    $(this.elementRef.nativeElement).find('#modal-buscar-producto').on('shown.bs.modal', () => {
      this.clearTable();
      this.loadProductos();
    });

    // Suscribirse al evento hidden.bs.modal
    $(this.elementRef.nativeElement).find('#modal-buscar-producto').on('hidden.bs.modal', () => {
      // Limpiar la tabla cuando se oculta el modal
      this.clearTable();
    });
  }


  loadProductos(): void{
    this.loading = true;
    this.productoService.getProductosActivosSP().subscribe(
      productos => {
        this.productos = productos;
        this.loading = false;
        this.cdr.detectChanges();
        this.jqueryConfigs.configDataTableModal("productos");
        // this.loading = false;
      }, error => {
        console.error('Error al cargar los productos:', error);
        this.loading = false;
      }
    );
  }

  chooseProducto(producto: Producto): void{
    this.producto.emit(producto);
  }

  clearTable(): void {
    this.productos = [];
  }

}
