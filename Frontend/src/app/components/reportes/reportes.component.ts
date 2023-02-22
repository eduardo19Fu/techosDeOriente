import { Component, OnInit } from '@angular/core';

import { ProductoService } from '../../services/producto.service';
import { FacturaService } from '../../services/facturas/factura.service';
import { EnvioService } from '../../services/envios/envio.service';

import Swal from 'sweetalert2';

@Component({
  selector: 'app-reportes',
  templateUrl: './reportes.component.html',
  styles: [
  ]
})
export class ReportesComponent implements OnInit {

  title: string;

  constructor(
    private productoService: ProductoService,
    private facturaService: FacturaService,
    private envioService: EnvioService
  ) {
    this.title = 'Módulo de Reportes';
  }

  ngOnInit(): void {
  }

}
