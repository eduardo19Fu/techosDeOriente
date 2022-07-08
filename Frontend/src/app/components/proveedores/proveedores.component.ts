import { Component, OnInit } from '@angular/core';

import { Proveedor } from 'src/app/models/proveedor';

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
    public auth: AuthService
  ) 
  {
    this.title = 'Listado de Proveedores';
  }

  ngOnInit(): void {
  }

  getProveedores(): void {}

}
