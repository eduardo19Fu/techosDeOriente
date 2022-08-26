import { Component, Input, OnInit } from '@angular/core';

import { Compra } from '../../../models/compra';
import { DetailService } from '../../../services/facturas/detail.service';

@Component({
  selector: 'app-modal-detalle-compra',
  templateUrl: './modal-detalle-compra.component.html',
  styleUrls: ['./modal-detalle-compra.component.css']
})
export class ModalDetalleCompraComponent implements OnInit {

  title: string;

  @Input() compra: Compra;

  constructor(
    public detailService: DetailService
  ) {
    this.title = 'Detalle de Compra';
  }

  ngOnInit(): void {
  }

  cerrarModal(): void{
    this.detailService.cerrarModal();
  }

}
