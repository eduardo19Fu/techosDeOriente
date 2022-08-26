import { Component, OnInit } from '@angular/core';

import { CompraService } from 'src/app/services/compras/compra.service';
import { Compra } from '../../models/compra';

import { JqueryConfigs } from '../../utils/jquery/jquery-utils';
import Swal from 'sweetalert2';
import { AuthService } from '../../services/auth.service';
import { DetailService } from '../../services/facturas/detail.service';

@Component({
  selector: 'app-compras',
  templateUrl: './compras.component.html',
  styles: [
  ]
})
export class ComprasComponent implements OnInit {

  title: string;

  compraSeleccionada: Compra;

  compras: Compra[];

  jqueryConfigs: JqueryConfigs;

  constructor(
    private compraService: CompraService,
    public authService: AuthService,
    public detailService: DetailService
  ) {
    this.title = 'Compras Realizadas';
    this.jqueryConfigs = new JqueryConfigs();
  }

  ngOnInit(): void {
    this.getCompras();
  }

  /**
   * Función que realiza la petición al backend para recuperar el listado de compras realizadas.
   * 
   */
  getCompras(): void {
    this.compraService.getCompras().subscribe(compras => {
      this.compras = compras;
      this.jqueryConfigs.configDataTable('compras');
    });
  }

  anularCompra(): void { }

  abrirDetalle(compra: Compra): void {
    this.compraSeleccionada = compra;
    this.detailService.abrirModal();
  }

}
