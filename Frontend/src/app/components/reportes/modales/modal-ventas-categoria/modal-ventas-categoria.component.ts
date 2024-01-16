import { Component, OnInit } from '@angular/core';
import { FacturaService } from 'src/app/services/facturas/factura.service';
import { TipoProductoService } from 'src/app/services/tipo-producto.service';

@Component({
  selector: 'app-modal-ventas-categoria',
  templateUrl: './modal-ventas-categoria.component.html',
  styleUrls: ['./modal-ventas-categoria.component.css']
})
export class ModalVentasCategoriaComponent implements OnInit {

  title: string;
  fecha: Date;

  constructor(
    private tipoProductoService: TipoProductoService,
    private facturaService: FacturaService
  ) {
    this.title = 'Ventas por Categoría de Producto';
  }

  ngOnInit(): void {
  }

  onSubmit(): void {}

}
