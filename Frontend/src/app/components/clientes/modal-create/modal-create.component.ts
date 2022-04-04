import { Component, EventEmitter, Input, OnChanges, OnInit, Output } from '@angular/core';

import { Cliente } from 'src/app/models/cliente';

import { ClienteService } from 'src/app/services/cliente.service';
import { ClienteCreateService } from 'src/app/services/facturas/cliente-create.service';

import swal from 'sweetalert2';

@Component({
  selector: 'app-modal-create',
  templateUrl: './modal-create.component.html',
  styleUrls: ['./modal-create.component.css']
})
export class ModalCreateComponent implements OnInit, OnChanges {

  title: string;

  cliente: Cliente;

  @Input() nit: string;

  @Output() clienteCreado = new EventEmitter<Cliente>();

  constructor(
    private clienteService: ClienteService,
    public clienteCreateService: ClienteCreateService
  ) {
    this.title = 'Registrar Cliente';
    this.cliente = new Cliente();
  }

  ngOnInit(): void {
  }

  ngOnChanges(): void{
    this.cliente.nit = this.nit;
  }

  cerrarModal(): void{
    this.clienteCreateService.cerrarModal();
  }

  create(): void{
    this.clienteService.create(this.cliente).subscribe(
      response => {
        this.clienteCreateService.cerrarModal();
        this.clienteCreado.emit(response.cliente);
        swal.fire('Cliente Registrado', `${response.mensaje}`, 'success');
      }
    );
  }

}
