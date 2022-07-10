import { Component, OnInit } from '@angular/core';
import { Pais } from 'src/app/models/pais';
import { PaisService } from 'src/app/services/paises/pais.service';

@Component({
  selector: 'app-paises',
  templateUrl: './paises.component.html',
  styles: [
  ]
})
export class PaisesComponent implements OnInit {

  title: string;

  paises: Pais[];

  constructor(
    private paisService: PaisService
  ) {
    this.title = 'Listado de Paises Disponibles';
  }

  ngOnInit(): void {
  }

  getPaises(): void {}

}
