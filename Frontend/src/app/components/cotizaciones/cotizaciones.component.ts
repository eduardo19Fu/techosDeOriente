import { Component, OnInit } from '@angular/core';

import { Cotizacion } from '../../models/cotizacion';
import { AuthService } from '../../services/auth.service';
import { CotizacionService } from '../../services/cotizaciones/cotizacion.service';
import { DetailService } from '../../services/facturas/detail.service';

import { JqueryConfigs } from 'src/app/utils/jquery/jquery-utils';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-cotizaciones',
  templateUrl: './cotizaciones.component.html',
  styleUrls: ['./cotizaciones.component.css']
})
export class CotizacionesComponent implements OnInit {

  title: string;

  cotizacionSeleccionada: Cotizacion;
  cotizaciones: Cotizacion[];

  jqueryConfig: JqueryConfigs;

  swalWithBootstrapButtons = Swal.mixin({
    customClass: {
      confirmButton: 'btn btn-success',
      cancelButton: 'btn btn-danger'
    },
    buttonsStyling: true
  });

  constructor(
    public auth: AuthService,
    public detailService: DetailService,
    private cotizacionService: CotizacionService
  ) 
  {
    this.title = 'Cotización de Productos';
    this.cotizaciones = [];
    this.jqueryConfig = new JqueryConfigs();
  }

  ngOnInit(): void {
    this.cargarCotizaciones();
  }

  cargarCotizaciones(): void {
    this.cotizacionService.getCotizaciones().subscribe(response => {
      this.cotizaciones = response;
      this.jqueryConfig.configDataTable('cotizaciones');
    });
  }

  abrirDetalle(cotizacion: Cotizacion): void {
    this.cotizacionSeleccionada = cotizacion;
    this.detailService.abrirModal();
  }

  print(cotizacion: Cotizacion): void {
    this.cotizacionService.getCotizacionPDF(cotizacion.idCotizacion).subscribe(response => {
      const url = window.URL.createObjectURL(response.data);
      const a = document.createElement('a');
      document.body.appendChild(a);
      a.setAttribute('style', 'display: none');
      a.setAttribute('target', 'blank');
      a.href = url;
      /*
        opcion para pedir descarga de la respuesta obtenida
        a.download = response.filename;
      */
      window.open(a.toString(), '_blank');
      window.URL.revokeObjectURL(url);
      a.remove();
    },
      error => {
        Swal.fire(`Error al crear factura para imprimir.`, error.message, 'error');
      });
  }

  delete(cotizacion: Cotizacion): void {
    this.swalWithBootstrapButtons.fire({
      title: '¿Está seguro?',
      text: `¿Seguro que desea eliminar la cotizacion ${cotizacion.idCotizacion}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: '¡Si, eliminar!',
      cancelButtonText: '¡No, cancelar!',
      reverseButtons: true
    }).then((result) => {
      if (result.isConfirmed) {

        this.cotizacionService.delete(cotizacion.idCotizacion).subscribe(
          response => {
            this.cotizaciones = this.cotizaciones.filter(cot => cot !== cotizacion);
            this.swalWithBootstrapButtons.fire(
              '¡Cotizacion Eliminada!',
              'La cotización ha sido eliminada con éxito!',
              'success'
            );
          }
        );
      } else if (
        result.dismiss === Swal.DismissReason.cancel
      ) {
        this.swalWithBootstrapButtons.fire(
          'Proceso Cancelado',
          'La cotización no fúe eliminada de la base de datos.',
          'error'
        );
      }
    });
  }

}
