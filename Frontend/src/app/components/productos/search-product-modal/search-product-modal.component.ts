import { Component, OnInit } from '@angular/core';
import { Producto } from 'src/app/models/producto';
import { ProductoService } from '../../../services/producto.service';
import { JqueryConfigs } from '../../../utils/jquery/jquery-utils';

@Component({
  selector: 'app-search-product-modal',
  templateUrl: './search-product-modal.component.html',
  styleUrls: ['./search-product-modal.component.css']
})
export class SearchProductModalComponent implements OnInit {

  title: string;
  productos: Producto[];

  jqueryConfig: JqueryConfigs;

  constructor(
    private productoService: ProductoService
  ) {
    this.title = 'Listado de Productos';
    this.jqueryConfig = new JqueryConfigs();
  }

  ngOnInit(): void {
    this.loadProductos();
  }

  loadProductos(): void{
    this.productoService.getProductos().subscribe(
      productos => {
        this.productos = productos;
        this.jqueryConfig.configDataTableModal('productos');
      }
    );
  }

}
