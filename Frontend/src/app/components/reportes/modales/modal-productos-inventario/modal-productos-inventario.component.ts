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
  fecha: Date;

  constructor(
    private productoService: ProductoService
  ) {
    this.title = 'Reporte de Inventario';
  }

  ngOnInit(): void {
  }

  onSubmit(): void {}

}
