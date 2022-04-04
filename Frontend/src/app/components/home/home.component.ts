import { Component, OnInit } from '@angular/core';

import { ClienteService } from 'src/app/services/cliente.service';
import { FacturaService } from 'src/app/services/facturas/factura.service';
import { MarcaProductoService } from 'src/app/services/marca-producto.service';
import { ProductoService } from 'src/app/services/producto.service';
import { UsuarioService } from 'src/app/services/usuarios/usuario.service';

import { Cliente } from 'src/app/models/cliente';
import { MarcaProducto } from 'src/app/models/marca-producto';
import { Producto } from 'src/app/models/producto';
import { Usuario } from 'src/app/models/usuario';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  public title: string;
  public totalProductos: number;
  public totalClientes: number;
  public totalMarcas: number;
  public totalUsuarios: number;
  public totalFacturas: number;

  productos: Producto[];
  clientes: Cliente[];
  marcas: MarcaProducto[];
  usuarios: Usuario[];

  constructor(
    private serviceProducto: ProductoService,
    private serviceCliente: ClienteService,
    private serviceMarca: MarcaProductoService,
    private serviceUsuario: UsuarioService,
    private serviceFactura: FacturaService
  ) {
    this.title = 'Inicio';
   }

  ngOnInit(): void {
    this.getProductos();
    this.getClientes();
    this.getMarcas();
    this.getUsuarios();
    this.getFacturas();
  }

  getProductos(): void{
    this.serviceProducto.getProductos().subscribe(productos => this.totalProductos = productos.length);
  }

  getClientes(): void{
    this.serviceCliente.getClientes().subscribe(clientes => this.totalClientes = clientes.length);
  }

  getMarcas(): void{
    this.serviceMarca.getMarcas().subscribe(marcas => this.totalMarcas = marcas.length);
  }

  getUsuarios(): void{
    this.serviceUsuario.getUsuarios().subscribe(usuarios => this.totalUsuarios = usuarios.length);
  }

  getFacturas(): void{
    this.serviceFactura.getFacturas().subscribe(facturas => this.totalFacturas = facturas.length);
  }

}
