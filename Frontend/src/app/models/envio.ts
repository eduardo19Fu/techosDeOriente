import { Cliente } from './cliente';
import { UsuarioAuxiliar } from './auxiliar/usuario-auxiliar';
import { DetalleEnvio } from './detalle-envio';
import { Factura } from './factura';
import { Estado } from './estado';

export class Envio {
    idEnvio: number;
    fechaPedido: Date;
    telefonoReferencia: string;
    totalEnvio: number;
    abono: number;
    saldoPendiente: number;
    fechaRegistro: Date;
    referencia: string;

    cliente: Cliente;
    usuario: UsuarioAuxiliar;
    factura: Factura;
    estados: Estado[] = [];
    itemsEnvio: DetalleEnvio[] = [];

    calcularTotal(): number {
        let total = 0;
        this.itemsEnvio.forEach((item: DetalleEnvio) => {
            total += item.calcularImporteDescuento();
        });

        return total;
    }

    calcularSaldoRestante(abono: number, totalEnvio: number): number {
        let saldoRestante = 0;
        saldoRestante = totalEnvio - abono;
        return saldoRestante;
    }
}
