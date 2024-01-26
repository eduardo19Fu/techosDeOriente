import { Estado } from './estado';
import { Proveedor } from './proveedor';
import { UsuarioAuxiliar } from './auxiliar/usuario-auxiliar';
import { DetallePedido } from './detalle-pedido';

export class Pedido {
    idPedido: number;
    totalPedido: number;
    fechaPedido: Date;
    fechaRegistro: Date;

    estado: Estado;
    proveedor: Proveedor;
    usuario: UsuarioAuxiliar;
    itemsPedido: DetallePedido[];
}
