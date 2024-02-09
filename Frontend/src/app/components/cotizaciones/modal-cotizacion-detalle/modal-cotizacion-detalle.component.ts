import { Component, Input, OnInit } from '@angular/core';
import { DetailService } from '../../../services/facturas/detail.service';
import { Cotizacion } from '../../../models/cotizacion';

@Component({
  selector: 'app-modal-cotizacion-detalle',
  templateUrl: './modal-cotizacion-detalle.component.html',
  styleUrls: ['./modal-cotizacion-detalle.component.css']
})
export class ModalCotizacionDetalleComponent implements OnInit {

  title: string;

  @Input() cotizacion: Cotizacion;

  constructor(
    public detailService: DetailService
  ) 
  {
    this.title = 'Detalle de Cotización';
  }

  ngOnInit(): void {
  }

  cerrarModal(): void{
    this.detailService.cerrarModal();
  }

}
