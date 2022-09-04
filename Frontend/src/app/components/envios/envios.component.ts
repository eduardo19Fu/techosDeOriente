import { Component, OnInit } from '@angular/core';

import { Envio } from 'src/app/models/envio';
import { AuthService } from 'src/app/services/auth.service';
import { EnvioService } from 'src/app/services/envios/envio.service';
import { DetailService } from 'src/app/services/facturas/detail.service';
import { JqueryConfigs } from 'src/app/utils/jquery/jquery-utils';

import Swal from 'sweetalert2';

@Component({
  selector: 'app-envios',
  templateUrl: './envios.component.html',
  styles: [
  ]
})
export class EnviosComponent implements OnInit {

  title: string;
  envioSeleccionado: Envio;

  envios: Envio[];

  jqueryConfigs: JqueryConfigs;

  constructor(
    private envioService: EnvioService,
    public auth: AuthService,
    public detailService: DetailService
  ) {
    this.title = 'Listado de Envios';
    this.jqueryConfigs = new JqueryConfigs();
  }

  ngOnInit(): void {
    this.getEnvios();
  }

  getEnvios(): void {
    this.envioService.getEnvios().subscribe(
      envios => {
        this.envios = envios;
        this.jqueryConfigs.configDataTable('envios');
      }
    );
  }

  abrirDetalle(envio: Envio): void {
    this.envioSeleccionado = envio;
    this.detailService.abrirModal();
  }

}
