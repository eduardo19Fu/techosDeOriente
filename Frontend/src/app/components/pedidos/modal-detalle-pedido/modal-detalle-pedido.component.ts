import { Component, Input, OnInit } from '@angular/core';
import { DetailService } from '../../../services/facturas/detail.service';
import { PedidosComponent } from '../pedidos.component';

@Component({
  selector: 'app-modal-detalle-pedido',
  templateUrl: './modal-detalle-pedido.component.html',
  styleUrls: ['./modal-detalle-pedido.component.css']
})
export class ModalDetallePedidoComponent implements OnInit {

  title: string;

  @Input() pedido: PedidosComponent;

  constructor(
    public detailService: DetailService
  ) {
    this.title = 'Detalles del Pedido';
  }

  ngOnInit(): void {
  }

  cerrarModal(): void{
    this.detailService.cerrarModal();
  }

}
