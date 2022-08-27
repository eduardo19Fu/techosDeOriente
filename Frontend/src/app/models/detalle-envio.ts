import { Producto } from './producto';

export class DetalleEnvio {
    idDetalleEnvio: number;
    subTotal: number;
    cantidad: number;

    producto: Producto;

    calcularTotalEnvio(): number {
        return 0;
    }
}
