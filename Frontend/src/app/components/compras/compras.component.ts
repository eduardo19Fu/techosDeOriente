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

  swalWithBootstrapButtons = Swal.mixin({
    customClass: {
      confirmButton: 'btn btn-success',
      cancelButton: 'btn btn-danger'
    },
    buttonsStyling: true
  });

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

  delete(compra: Compra): void {
    this.swalWithBootstrapButtons.fire({
      title: '¿Está seguro?',
      text: `¿Seguro que desea eliminar la compra ${compra.idCompra}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: '¡Si, eliminar!',
      cancelButtonText: '¡No, cancelar!',
      reverseButtons: true
    }).then((result) => {
      if (result.isConfirmed) {

        this.compraService.delete(compra.idCompra).subscribe(
          response => {
            this.compras = this.compras.filter(com => com !== compra);
            this.swalWithBootstrapButtons.fire(
              'Compra Eliminada!',
              'La compra ha sido eliminado con éxito!',
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

}
