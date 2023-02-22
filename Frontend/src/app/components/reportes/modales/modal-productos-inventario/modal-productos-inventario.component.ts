import { Component, OnInit } from '@angular/core';

import { ProductoService } from '../../../../services/producto.service';

import Swal from 'sweetalert2';

@Component({
  selector: 'app-modal-productos-inventario',
  templateUrl: './modal-productos-inventario.component.html',
  styleUrls: ['./modal-productos-inventario.component.css']
})
export class ModalProductosInventarioComponent implements OnInit {

  title: string;
  fecha: Date = new Date();

  constructor(
    private productoService: ProductoService
  ) {
    this.title = 'Reporte de Inventario';
  }

  ngOnInit(): void {
  }

  onSubmit(): void {
    if (this.fecha) {
      this.generarInventario();
    }
  }

  generarInventario(): void {
    this.productoService.printInventarioReport(this.fecha).subscribe(response => {
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
