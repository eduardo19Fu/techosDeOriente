import { Proveedor } from './proveedor';
import { Estado } from './estado';
import { TipoComprobante } from './tipo-comprobante';
import { DetalleCompra } from './detalle-compra';
import { UsuarioAuxiliar } from './auxiliar/usuario-auxiliar';
export class Compra {
    idCompra: number;
    noComprobante: string;
    iva: number;
    totalCompra: number;
    fechaCompra: Date;

    proveedor: Proveedor
    estado: Estado;
    tipoComprobante: TipoComprobante;
    usuario: UsuarioAuxiliar;
    items: DetalleCompra[] = [];
}
