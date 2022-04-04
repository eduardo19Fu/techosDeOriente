import { Producto } from './producto';
import { UsuarioAuxiliar } from './auxiliar/usuario-auxiliar';

export class MovimientoProducto {
    idMovimiento: number;
    tipoMovimiento: string;
    cantidad: number;
    stockInicial: number;
    fechaMovimiento: Date;

    producto: Producto;
    usuario: UsuarioAuxiliar;
}
