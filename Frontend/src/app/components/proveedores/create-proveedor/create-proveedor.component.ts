import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

import { Proveedor } from '../../../models/proveedor';
import { Pais } from '../../../models/pais';

import { ProveedorService } from '../../../services/proveedores/proveedor.service';
import { PaisService } from '../../../services/paises/pais.service';

import Swal from 'sweetalert2';
@Component({
  selector: 'app-create-proveedor',
  templateUrl: './create-proveedor.component.html',
  styles: [
  ]
})
export class CreateProveedorComponent implements OnInit {

  title: string;

  proveedor: Proveedor;

  paises: Pais[];

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
    this.cargarPaises();
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
    console.log(this.proveedor);
    this.proveedorService.create(this.proveedor).subscribe(res => 
      {
        console.log(res.proveedor);
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

  cargarPaises(): void 
  {
    this.paisService.getPaises().subscribe(paises => this.paises = paises);
  }

  // Comparar para reemplazar el valor en el select del formulario en caso de existir
  compararMarca(o1: Pais, o2: Pais): boolean {
    if (o1 === undefined && o2 === undefined) {
      return true;
    }
    return o1 === null || o2 === null || o1 === undefined || o2 === undefined ? false : o1.idPais === o2.idPais;
  }

}
