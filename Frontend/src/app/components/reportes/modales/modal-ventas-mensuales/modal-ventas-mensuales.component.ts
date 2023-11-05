import { Component, OnInit } from '@angular/core';
import { catchError } from 'rxjs/operators';
import { FacturaService } from '../../../../services/facturas/factura.service';

import Swal from 'sweetalert2';

@Component({
  selector: 'app-modal-ventas-mensuales',
  templateUrl: './modal-ventas-mensuales.component.html',
  styleUrls: ['./modal-ventas-mensuales.component.css']
})
export class ModalVentasMensualesComponent implements OnInit {

  title: string;
  anho: string;

  constructor(
    private facturaService: FacturaService
  ) {
    this.title = 'Ventas Mensuales';
  }

  ngOnInit(): void {
  }

  onSubmit(): void {
    if (this.anho.length > 0) {
      this.generarInformeVentasMensuales();
    } else {
      Swal.fire('', 'Ingrese un valor válido para el año', 'warning');
    }
  }

  generarInformeVentasMensuales(): void {
    this.facturaService.getMonthlySellsReportPDF(parseInt(this.anho)).subscribe(
      response => {
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
        console.log('ERROR AL GENERAR INFORME DE VENTAS MENSUALES');
        Swal.fire('Error al Generar el Reporte', err.error, 'error');
      }
    );
  }

}
