import { Component, OnInit } from '@angular/core';

import { Proveedor } from 'src/app/models/proveedor';
import { ProveedorService } from 'src/app/services/proveedores/proveedor.service';

import { JqueryConfigs } from '../../utils/jquery/jquery-utils';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-proveedores',
  templateUrl: './proveedores.component.html',
  styles: [
  ]
})
export class ProveedoresComponent implements OnInit {

  title: string;

  jqueryConfigs: JqueryConfigs = new JqueryConfigs();

  proveedores: Proveedor[];

  constructor
  (
    public auth: AuthService,
    private proveedorService: ProveedorService
  ) 
  {
    this.title = 'Listado de Proveedores';
  }

  ngOnInit(): void {
    this.getProveedores();
  }

  getProveedores(): void {
    this.proveedorService.getProveedores().subscribe(
      proveedores => {
        this.proveedores = proveedores;
        this.jqueryConfigs.configDataTable('proveedores');
      }
    );
  }

}
