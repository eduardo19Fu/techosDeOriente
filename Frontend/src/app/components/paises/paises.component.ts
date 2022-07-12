import { Component, OnInit } from '@angular/core';

import { Pais } from 'src/app/models/pais';
import { PaisService } from 'src/app/services/paises/pais.service';
import { AuthService } from '../../services/auth.service';

import { JqueryConfigs } from '../../utils/jquery/jquery-utils';

@Component({
  selector: 'app-paises',
  templateUrl: './paises.component.html',
  styles: [
  ]
})
export class PaisesComponent implements OnInit {

  title: string;
  jqueryConfigs: JqueryConfigs;

  paises: Pais[];

  constructor(
    public auth: AuthService,
    private paisService: PaisService
  ) {
    this.title = 'Listado de Paises Disponibles';
    this.jqueryConfigs = new JqueryConfigs();
  }

  ngOnInit(): void {
    this.getPaises();
  }

  getPaises(): void {
    this.paisService.getPaises().subscribe(
      paises => {
        this.paises = paises;
        this.jqueryConfigs.configDataTable('paises');
      }
    );
  }

}
