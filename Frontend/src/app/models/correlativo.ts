import { UsuarioAuxiliar } from './auxiliar/usuario-auxiliar';
import { Estado } from './estado';

export class Correlativo {

    idCorrelativo: number;
    correlativoInicial: number;
    correlativoFinal: number;
    correlativoActual: number;
    serie: string;
    fechaCreacion: Date;

    estado: Estado;
    usuario: UsuarioAuxiliar;
}
