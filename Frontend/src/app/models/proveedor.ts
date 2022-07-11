import { Estado } from './estado';
import { Pais } from './pais';

export class Proveedor {
    idProveedor: number;
    nombre: string;
    contacto: string;
    web: string;
    email: string;
    telefono: string;
    direccion: string;
    fechaRegistro: Date;

    estado: Estado;
    pais: Pais;
}
