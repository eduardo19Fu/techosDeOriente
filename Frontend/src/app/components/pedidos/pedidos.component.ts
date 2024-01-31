import { Component, OnInit } from '@angular/core';

import { Pedido } from '../../models/pedido';
import { AuthService } from '../../services/auth.service';
import { PedidoService } from '../../services/pedidos/pedido.service';
import { JqueryConfigs } from '../../utils/jquery/jquery-utils';

import Swal from 'sweetalert2'; 
import { DetailService } from 'src/app/services/facturas/detail.service';

@Component({
  selector: 'app-pedidos',
  templateUrl: './pedidos.component.html',
  styleUrls: ['./pedidos.component.css']
})
export class PedidosComponent implements OnInit {

  title: string;

  pedidoSeleccionado: Pedido;
  pedidos: Pedido[];
  jqueryConfigs: JqueryConfigs;

  swalWithBootstrapButtons = Swal.mixin({
    customClass: {
      confirmButton: 'btn btn-success',
      cancelButton: 'btn btn-danger'
    },
    buttonsStyling: true
  });

  constructor(
    public authService: AuthService,
    private pedidoService: PedidoService,
    public detailService: DetailService
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

  abrirDetalle(pedido: Pedido): void {
    this.pedidoSeleccionado = pedido;
    this.detailService.abrirModal();
  }

  eliminarPedido(pedido: Pedido): void {
    this.swalWithBootstrapButtons.fire({
      title: '¿Está seguro?',
      text: `¿Seguro que desea eliminar el pedido #${pedido.idPedido}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: '¡Si, eliminar!',
      cancelButtonText: '¡No, cancelar!',
      reverseButtons: true
    }).then((result) => {
      if (result.isConfirmed) {

        this.pedidoService.delete(pedido.idPedido).subscribe(
          response => {
            this.pedidos = this.pedidos.filter(ped => ped !== pedido);
            this.swalWithBootstrapButtons.fire(
              '¡Pedido Eliminado!',
              `${response.mensaje}`,
              'success'
            );
          }
        );
      } else if (
        /* Read more about handling dismissals below */
        result.dismiss === Swal.DismissReason.cancel
      ) {
        this.swalWithBootstrapButtons.fire(
          'Proceso Cancelado',
          'La compra no fúe eliminado de la base de datos.',
          'error'
        );
      }
    });
  }

  /** GENERAR REPORTE CON EL PEDIDO RECIÉN CREADO **/
  generarPedido(pedido: Pedido): void {
    this.pedidoService.getPedidoPDF(pedido.idPedido).subscribe(response => {
      const url = window.URL.createObjectURL(response.data);
      const a = document.createElement('a');
      document.body.appendChild(a);
      a.setAttribute('style', 'display: none');
      a.setAttribute('target', 'blank');
      a.href = url;

      window.open(a.toString(), '_blank');
      window.URL.revokeObjectURL(url);
      a.remove();
    },
      error => {
        console.log(error);
    });
  }

}
