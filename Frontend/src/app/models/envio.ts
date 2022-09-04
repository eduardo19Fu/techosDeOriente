import { Cliente } from './cliente';
import { UsuarioAuxiliar } from './auxiliar/usuario-auxiliar';
import { DetalleEnvio } from './detalle-envio';
import { Usuario } from './usuario';

export class Envio {
    idEnvio: number;
    fechaPedido: Date;
    telefonoReferencia: string;
    abono: number;
    saldoPendiente: number;
    fechaRegistro: Date;
    referencia: string;

    cliente: Cliente;
    usuario: Usuario;
    itemsEnvio: DetalleEnvio[] = [];
}
