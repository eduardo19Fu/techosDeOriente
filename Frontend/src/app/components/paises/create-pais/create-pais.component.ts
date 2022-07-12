import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

import { PaisService } from '../../../services/paises/pais.service';
import { Pais } from '../../../models/pais';

import Swal from 'sweetalert2';

@Component({
  selector: 'app-create-pais',
  templateUrl: './create-pais.component.html',
  styles: [
  ]
})
export class CreatePaisComponent implements OnInit {

  title: string;

  pais: Pais;

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private paisService: PaisService
  ) 
  {
    this.title = 'Registro de País';
    this.pais = new Pais();
  }

  ngOnInit(): void {
    this.cargarPais();
  }

  cargarPais(): void 
  {
    this.activatedRoute.params.subscribe(params => 
      {
        const id = params['id'];
        if (id) 
        {
          this.paisService.getPais(id).subscribe(pais => this.pais = pais);
        }
      });
  }

  create(): void 
  {
    this.paisService.create(this.pais).subscribe(res => 
      {
        this.router.navigate(['/paises/index']);
        Swal.fire(res.mensaje, `El país ${res.pais.nombre} fue registrado en la Base de Datos.`, 'success');
      });
  }

  update(): void 
  {
    this.paisService.update(this.pais).subscribe(res => 
      {
        this.router.navigate(['/paises/index']);
        Swal.fire(res.mensaje, `Los datos de ${res.pais.nombre} fueron actualizados.`, 'success');
      });
  }

}
