import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

import { ProveedorService } from '../../../services/proveedores/proveedor.service';
import { PaisService } from '../../../services/paises/pais.service';

import Swal from 'sweetalert2';
import { Proveedor } from '../../../models/proveedor';

@Component({
  selector: 'app-create-proveedor',
  templateUrl: './create-proveedor.component.html',
  styles: [
  ]
})
export class CreateProveedorComponent implements OnInit {

  title: string;

  proveedor: Proveedor;

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private proveedorService: ProveedorService,
    private paisService: PaisService
  ) 
  {
    this.title = 'Registro de Proveedor';
    this.proveedor = new Proveedor();
  }

  ngOnInit(): void {
    this.cargarProveedor();
  }

  cargarProveedor(): void 
  {
    this.activatedRoute.params.subscribe(params => 
      {
        const id = params['id'];
        if (id) 
        {
          this.proveedorService.getProveedor(id).subscribe(
            proveedor => this.proveedor = proveedor
          );
        }
      });
  }

  create(): void 
  {
    this.proveedorService.create(this.proveedor).subscribe(res => 
      {
        this.router.navigate(['/proveedores/index']);
        Swal.fire(res.mensaje, `El proveedor ${res.proveedor.nombre} fue registrado en la Base de Datos.`, 'success');
      });
  }

  update(): void {
    this.proveedorService.update(this.proveedor).subscribe(res => 
      {
        this.router.navigate(['/proveedores/index']);
        Swal.fire(res.mensaje, `Los datos del proveedor ${res.proveedor.nombre} fueron actualizados.`, 'success');
      });
  }

}
