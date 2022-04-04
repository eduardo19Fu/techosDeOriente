import { Component, OnInit, AfterViewInit } from '@angular/core';
import { Usuario } from 'src/app/models/usuario';
import { UsuarioService } from '../../../services/usuarios/usuario.service';
import { FacturaService } from '../../../services/facturas/factura.service';
import { AuthService } from '../../../services/auth.service';
import { UsuarioAuxiliar } from '../../../models/auxiliar/usuario-auxiliar';

@Component({
  selector: 'app-poliza-individual',
  templateUrl: './poliza-individual.component.html',
  styleUrls: ['./poliza-individual.component.css']
})
export class PolizaIndividualComponent implements OnInit, AfterViewInit {

  title: string;

  fecha: Date;

  usuario: UsuarioAuxiliar;
  cajeros: Usuario[];

  constructor(
    private usuarioService: UsuarioService,
    private facturaService: FacturaService,
    private authService: AuthService
  ) {
    this.title = 'Poliza Individual';
    this.usuario = new UsuarioAuxiliar();
  }

  ngOnInit(): void {
  }

  ngAfterViewInit(): void {
    this.getCajeros();
  }

  getUsuario(event): void{
    this.usuarioService.getUsuario(event).subscribe(
      usuario => this.usuario = usuario
    );
  }

  onSubmit(): void {
    this.facturaService.getSellsDaillyReportPDF(this.usuario.idUsuario, this.fecha).subscribe(response => {
      const url = window.URL.createObjectURL(response.data);
      const a = document.createElement('a');
      document.body.appendChild(a);
      a.setAttribute('style', 'display: none');
      a.setAttribute('target', 'blank');
      a.href = url;
      /*
        opcion para pedir descarga de la respuesta obtenida
        a.download = response.filename;
      */
      window.open(a.toString(), '_blank');
      window.URL.revokeObjectURL(url);
      a.remove();
    },
      error => {
        console.log(error);
      });
  }

  getCajeros(): void {
    this.usuarioService.getCajeros().subscribe(cajeros => this.cajeros = cajeros);
  }

}
