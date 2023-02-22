import { Component, OnInit } from '@angular/core';
import { FacturaService } from '../../../../services/facturas/factura.service';

@Component({
  selector: 'app-modal-ventas-diarias',
  templateUrl: './modal-ventas-diarias.component.html',
  styleUrls: ['./modal-ventas-diarias.component.css']
})
export class ModalVentasDiariasComponent implements OnInit {

  title: string;
  fecha: Date;

  constructor(
    private facturaService: FacturaService
  ) {
    this.title = 'Reporte de Ventas Diarias';
  }

  ngOnInit(): void {
  }

  onSubmit(): void {}

}
