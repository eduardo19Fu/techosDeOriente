import { Component, OnInit } from '@angular/core';

import { CompraService } from 'src/app/services/compras/compra.service';
import { Compra } from '../../models/compra';

import { JqueryConfigs } from '../../utils/jquery/jquery-utils';
import Swal from 'sweetalert2';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-compras',
  templateUrl: './compras.component.html',
  styles: [
  ]
})
export class ComprasComponent implements OnInit {

  title: string;

  compras: Compra[];

  jqueryConfigs: JqueryConfigs;

  constructor(
    private compraService: CompraService,
    public authService: AuthService
  ) {
    this.title = 'Compras Realizadas';
    this.jqueryConfigs = new JqueryConfigs();
  }

  ngOnInit(): void {
    this.getCompras();
  }

  getCompras(): void {
    this.compraService.getCompras().subscribe(compras => {
      this.compras = compras;
      console.log(this.compras);
      this.jqueryConfigs.configDataTable('compras');
    });
  }

  anularCompra(): void { }

}
