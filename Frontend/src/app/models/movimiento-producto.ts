import { Producto } from './producto';
import { UsuarioAuxiliar } from './auxiliar/usuario-auxiliar';
import { TipoMovimiento } from './tipo-movimiento';

export class MovimientoProducto {
    idMovimiento: number;
    cantidad: number;
    stockInicial: number;
    fechaMovimiento: Date;

    tipoMovimiento: TipoMovimiento;
    producto: Producto;
    usuario: UsuarioAuxiliar;
}
