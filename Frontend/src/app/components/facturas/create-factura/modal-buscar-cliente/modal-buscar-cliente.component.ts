import { Component, OnInit, EventEmitter, Output } from '@angular/core';

import { Cliente } from 'src/app/models/cliente';
import { ClienteService } from '../../../../services/cliente.service';

import { JqueryConfigs } from 'src/app/utils/jquery/jquery-utils';

@Component({
  selector: 'app-modal-buscar-cliente',
  templateUrl: './modal-buscar-cliente.component.html',
  styles: [
  ]
})
export class ModalBuscarClienteComponent implements OnInit {

  @Output() cliente = new EventEmitter<Cliente>();

  title: string;
  clientes: Cliente[];

  jqueryConfigs: JqueryConfigs = new JqueryConfigs();

  constructor(
    private clienteService: ClienteService
  ) {
    this.title = 'Búsqueda de Clientes';
  }

  ngOnInit(): void {
    this.loadClientes();
  }

  loadClientes(): void{
    this.clienteService.getClientes().subscribe(
      clientes => {
        this.clientes = clientes;
        this.jqueryConfigs.configDataTableModal('clientes');
      }
    );
  }

  chooseCliente(cliente: Cliente): void {
    this.cliente.emit(cliente);
    console.log(cliente);
  }

}
