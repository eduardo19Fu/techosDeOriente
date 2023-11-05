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

  onSubmit(): void {
    this.generarInformeEnviosRealizados();
  }

  generarInformeEnviosRealizados(): void {
    this.envioService.getEnviosRealizadosPdf(this.fecha).subscribe(response => {
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
