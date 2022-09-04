import { Component, Input, OnInit } from '@angular/core';
import { Envio } from 'src/app/models/envio';
import { DetailService } from 'src/app/services/facturas/detail.service';

@Component({
  selector: 'app-detail-envio',
  templateUrl: './detail-envio.component.html',
  styleUrls: ['./detail-envio.component.css']
})
export class DetailEnvioComponent implements OnInit {

  @Input() envio: Envio;

  title: string;

  constructor(
    public detailService: DetailService
  ) {
    this.title = 'Detalles del Envío';
  }

  ngOnInit(): void {
  }

  cerrarModal(): void{
    this.detailService.cerrarModal();
  }

}
