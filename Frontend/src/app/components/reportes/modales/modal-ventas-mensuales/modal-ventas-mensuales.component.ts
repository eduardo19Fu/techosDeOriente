import { Component, OnInit } from '@angular/core';

import { FacturaService } from '../../../../services/facturas/factura.service';

import Swal from 'sweetalert2';

@Component({
  selector: 'app-modal-ventas-mensuales',
  templateUrl: './modal-ventas-mensuales.component.html',
  styleUrls: ['./modal-ventas-mensuales.component.css']
})
export class ModalVentasMensualesComponent implements OnInit {

  title: string;
  fechaIni: Date;
  fechaFin: Date;

  constructor(
    private facturaService: FacturaService
  ) {
    this.title = 'Ventas Mensuales';
  }

  ngOnInit(): void {
  }

  onSubmit(): void {
    
  }

}
