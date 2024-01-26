import { Component, OnInit } from '@angular/core';

import { Pedido } from '../../models/pedido';
import { AuthService } from '../../services/auth.service';
import { PedidoService } from '../../services/pedidos/pedido.service';
import { JqueryConfigs } from '../../utils/jquery/jquery-utils';

import Swal from 'sweetalert2'; 

@Component({
  selector: 'app-pedidos',
  templateUrl: './pedidos.component.html',
  styleUrls: ['./pedidos.component.css']
})
export class PedidosComponent implements OnInit {

  title: string;

  pedidos: Pedido[];
  jqueryConfigs: JqueryConfigs;

  constructor(
    public authService: AuthService,
    private pedidoService: PedidoService
  ) { 
    this.title = "Pedidos";
    this.pedidos = [];
    this.jqueryConfigs = new JqueryConfigs();
  }

  ngOnInit(): void {
    this.cargarPedidos();
  }

  cargarPedidos(): void {
    this.pedidoService.getPedidos().subscribe(
      pedidos => {
        this.pedidos = pedidos;
        this.jqueryConfigs.configDataTable("pedidos");
      }
    );
  }

}
