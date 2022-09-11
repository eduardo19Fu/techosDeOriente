import { Component, OnInit } from '@angular/core';
import { EnvioService } from '../../../../services/envios/envio.service';

@Component({
  selector: 'app-modal-envios-realizados',
  templateUrl: './modal-envios-realizados.component.html',
  styleUrls: ['./modal-envios-realizados.component.css']
})
export class ModalEnviosRealizadosComponent implements OnInit {

  title: string;
  fecha: Date;

  constructor(
    private envioService: EnvioService
  ) {
    this.title = 'Reporte de Envíos Realizados';
  }

  ngOnInit(): void {
  }

  onSubmit(): void {}

}
