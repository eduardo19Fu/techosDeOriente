import { Component, Input, OnInit } from '@angular/core';
import { Factura } from 'src/app/models/factura';
import { ClienteCreateService } from '../../../../services/facturas/cliente-create.service';
import { FacturaService } from '../../../../services/facturas/factura.service';
import { ModalCambioService } from '../../../../services/facturas/modal-cambio.service';

import swal from 'sweetalert2';

@Component({
  selector: 'app-modal-cambio',
  templateUrl: './modal-cambio.component.html',
  styleUrls: ['./modal-cambio.component.css']
})
export class ModalCambioComponent implements OnInit {

  @Input() factura: Factura;

  efectivo: number;
  cambio: number;

  constructor(
    private facturaService: FacturaService,
    public modalCambioService: ModalCambioService
  ) { }

  ngOnInit(): void {
  }

  calcularCambio(event): void{
    console.log(event);
  }

  create(): void{
    this.facturaService.create(this.factura).subscribe(
      response => {
        // this.cliente = new Cliente();
        // this.factura = new Factura();
        // this.cargarCorrelativo();
        (document.getElementById('buscar') as HTMLInputElement).value = '';
        swal.fire('Venta Realizada', `Factura No. ${response.factura.noFactura} creada con éxito!`, 'success');
        (document.getElementById('buscar') as HTMLInputElement).focus();

        this.facturaService.getBillPDF(response.factura.idFactura).subscribe(res => {
          const url = window.URL.createObjectURL(res.data);
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
          console.log(error);
        });
      }
    );
  }

}
