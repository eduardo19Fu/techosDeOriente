import { Estado } from './estado';
import { Pais } from './pais';

export class Proveedor {
    idProveedor: number;
    contacto: string;
    web: string;
    email: string;
    telefono: string;
    direccion: string;
    fechaRegistro: Date;

    estado: Estado;
    pais: Pais;
}
