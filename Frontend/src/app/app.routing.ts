import { ModuleWithProviders } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { LoginComponent } from './components/login/login.component';
import { ErrorComponent } from './components/error/error.component';
import { ProductosComponent } from './components/productos/productos.component';
import { HomeComponent } from './components/home/home.component';
import { MarcasProductoComponent } from './components/marcas-producto/marcas-producto.component';
import { CreateProductoComponent } from './components/productos/create-producto/create-producto.component';
import { CreateMarcaComponent } from './components/marcas-producto/create-marca/create-marca.component';
import { TiposProductoComponent } from './components/tipos-producto/tipos-producto.component';
import { CreateTipoComponent } from './components/tipos-producto/create-producto/create-tipo.component';
import { FacturasComponent } from './components/facturas/facturas.component';
import { CreateFacturaComponent } from './components/facturas/create-factura/create-factura.component';
import { CorrelativosComponent } from './components/correlativos/correlativos.component';
import { CreateCorrelativoComponent } from './components/correlativos/create-correlativo/create-correlativo.component';
import { ClientesComponent } from './components/clientes/clientes.component';
import { CreateClienteComponent } from './components/clientes/create-cliente/create-cliente.component';
import { UsuariosComponent } from './components/usuarios/usuarios.component';
import { CreateUsuarioComponent } from './components/usuarios/create-usuario/create-usuario.component';
import { MovimientosProductoComponent } from './components/movimientos-producto/movimientos-producto.component';
import { CreateMovimientoComponent } from './components/movimientos-producto/create-movimiento/create-movimiento.component';
import { ProveedoresComponent } from './components/proveedores/proveedores.component';
import { CreateProveedorComponent } from './components/proveedores/create-proveedor/create-proveedor.component';

import { AuthGuard } from './components/usuarios/guards/auth.guard';
import { RoleGuard } from './components/usuarios/guards/role.guard';
import { PaisesComponent } from './components/paises/paises.component';
import { CreatePaisComponent } from './components/paises/create-pais/create-pais.component';
import { ComprasComponent } from './components/compras/compras.component';
import { CreateCompraComponent } from './components/compras/create-compra/create-compra.component';
import { EnviosComponent } from './components/envios/envios.component';
import { CreateEnvioComponent } from './components/envios/create-envio/create-envio.component';
import { ReportesComponent } from './components/reportes/reportes.component';
import { PedidosComponent } from './components/pedidos/pedidos.component';
import { CreatePedidoComponent } from './components/pedidos/create-pedido/create-pedido.component';
import { CotizacionesComponent } from './components/cotizaciones/cotizaciones.component';
import { CreateCotizacionComponent } from './components/cotizaciones/create-cotizacion/create-cotizacion.component';

const appRoutes: Routes = [
    {path: '', component: LoginComponent},
    {path: 'login', component: LoginComponent},
    {path: 'home', component: HomeComponent, canActivate: [AuthGuard]},

    /****** MENUS DE PRODUCTOS ******/
    {path: 'productos/index', component: ProductosComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN', 'ROLE_INVENTARIO', 'ROLE_COBRADOR']}},
    {path: 'productos/create', component: CreateProductoComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN', 'ROLE_INVENTARIO']}},
    {path: 'productos/create/:id', component: CreateProductoComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN', 'ROLE_INVENTARIO']}},
    {path: 'productos/marcas/index', component: MarcasProductoComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN', 'ROLE_INVENTARIO']}},
    {path: 'productos/marcas/create', component: CreateMarcaComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN', 'ROLE_INVENTARIO']}},
    {path: 'productos/marcas/create/:id', component: CreateMarcaComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN', 'ROLE_INVENTARIO']}},
    {path: 'productos/categorias/index', component: TiposProductoComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN', 'ROLE_INVENTARIO']}},
    {path: 'productos/categorias/create', component: CreateTipoComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN', 'ROLE_INVENTARIO']}},
    {path: 'productos/categorias/create/:id', component: CreateTipoComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN', 'ROLE_INVENTARIO']}},
    {path: 'productos/inventario/index', component: MovimientosProductoComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN', 'ROLE_INVENTARIO']}},
    {path: 'productos/inventario/create', component: CreateMovimientoComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN', 'ROLE_INVENTARIO']}},

    /****** MENUS DE COTIZACIONES ******/
    {path: 'cotizaciones/index', component: CotizacionesComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN', 'ROLE_COBRADOR']}},
    {path: 'cotizaciones/create', component: CreateCotizacionComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN', 'ROLE_COBRADOR']}},
    {path: 'cotizaciones/create/:id', component: CreateCotizacionComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN', 'ROLE_COBRADOR']}},


    /****** MENUS DE FACTURAS ******/
    {path: 'facturas/index', component: FacturasComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN', 'ROLE_COBRADOR']}},
    {path: 'facturas/create', component: CreateFacturaComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN', 'ROLE_COBRADOR']}},
    {path: 'facturas/create/:envio', component: CreateFacturaComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN', 'ROLE_COBRADOR']}},
    {path: 'facturas/correlativos/index', component: CorrelativosComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN']}},
    {path: 'facturas/correlativos/create', component: CreateCorrelativoComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN']}},
    {path: 'facturas/correlativos/create/:id', component: CreateCorrelativoComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN']}},

    /****** MENUS DE CLIENTES ******/
    {path: 'clientes/index', component: ClientesComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN', 'ROLE_COBRADOR']}},
    {path: 'clientes/create', component: CreateClienteComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN', 'ROLE_COBRADOR']}},
    {path: 'clientes/create/:id', component: CreateClienteComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN', 'ROLE_COBRADOR']}},

    /****** MENUS DE COMPRAS ******/
    {path: 'compras/index', component: ComprasComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN', 'ROLE_INVENTARIO']}},
    {path: 'compras/create', component: CreateCompraComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN', 'ROLE_INVENTARIO']}},
    {path: 'compras/create/:id', component: CreateCompraComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN', 'ROLE_INVENTARIO']}},

    /****** MENUS DE ENVIOS ******/
    {path: 'envios/index', component: EnviosComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN', 'ROLE_INVENTARIO']}},
    {path: 'envios/create', component: CreateEnvioComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN', 'ROLE_INVENTARIO']}},
    {path: 'envios/create/:id', component: CreateEnvioComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN', 'ROLE_INVENTARIO']}},

    /****** MENUS DE PROVEEDORES ******/
    {path: 'proveedores/index', component: ProveedoresComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN', 'ROLE_INVENTARIO']}},
    {path: 'proveedores/create', component: CreateProveedorComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN', 'ROLE_INVENTARIO']}},
    {path: 'proveedores/create/:id', component: CreateProveedorComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN', 'ROLE_INVENTARIO']}},

    /****** MENUS DE PEDIDOS ******/
    {path: 'pedidos/index', component: PedidosComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN', 'ROLE_INVENTARIO']}},
    {path: 'pedidos/create', component: CreatePedidoComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN', 'ROLE_INVENTARIO']}},
    {path: 'pedidos/create/:id', component: CreatePedidoComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN', 'ROLE_INVENTARIO']}},
    
    /****** MENUS DE CLIENTES ******/
    {path: 'paises/index', component: PaisesComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN', 'ROLE_INVENTARIO']}},
    {path: 'paises/create', component: CreatePaisComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN', 'ROLE_INVENTARIO']}},
    {path: 'paises/create/:id', component: CreatePaisComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN', 'ROLE_INVENTARIO']}},

    /****** MENUS DE USUARIOS ******/
    {path: 'usuarios/index', component: UsuariosComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN']}},
    {path: 'usuarios/create', component: CreateUsuarioComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN']}},
    {path: 'usuarios/create/:id', component: CreateUsuarioComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN']}},

    /****** MENU DE REPORTES ******/
    {path: 'reportes/main', component: ReportesComponent, canActivate: [AuthGuard, RoleGuard], data: {role: ['ROLE_ADMIN']}},

    {path: '**', component: ErrorComponent}
];

export const appRoutingProviders: any[] = [];
export const routing: ModuleWithProviders<any> = RouterModule.forRoot(appRoutes);
