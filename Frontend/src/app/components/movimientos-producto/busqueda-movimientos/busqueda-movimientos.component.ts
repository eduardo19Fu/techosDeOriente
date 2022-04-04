import { Component, OnInit } from '@angular/core';
import { MovimientosProductoService } from '../../../services/movimientos/movimientos-producto.service';

@Component({
  selector: 'app-busqueda-movimientos',
  templateUrl: './busqueda-movimientos.component.html',
  styleUrls: ['./busqueda-movimientos.component.css']
})
export class BusquedaMovimientosComponent implements OnInit {

  title: string;

  fechaIni: Date;
  fechaFin: Date;

  constructor(
    private movimientosProductoService: MovimientosProductoService
  ) {
    this.title = 'Reporte de Inventario';
  }

  ngOnInit(): void {
  }

  onSubmit(): void {
    this.movimientosProductoService.getInventoryPDF(this.fechaIni, this.fechaFin).subscribe(response => {
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
        console.log(error);
      });
  }

}
