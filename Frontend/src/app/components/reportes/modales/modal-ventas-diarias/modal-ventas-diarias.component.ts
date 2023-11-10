import { Component, OnInit } from '@angular/core';
import { FacturaService } from '../../../../services/facturas/factura.service';

import Swal from 'sweetalert2';

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

  onSubmit(): void {
    this.getResumentVentasDiarias();
  }

  getResumentVentasDiarias(): void {
    this.facturaService.getAllDailySales(this.fecha).subscribe(response => {
      const url = window.URL.createObjectURL(response.data);
      const a = document.createElement('a');
      document.body.appendChild(a);
      a.setAttribute('style', 'display: none');
      a.setAttribute('target', 'blank');
      a.href = url;

      window.open(a.toString(), '_blank');
      window.URL.revokeObjectURL(url);
      a.remove();
    }, err => {
      console.log('ERROR AL GENERAR RESUMEN DE VENTAS DIARIAS');
      console.log(err);
      Swal.fire('Error al Generar el Reporte', err.error, 'error');
    });
  }
}
