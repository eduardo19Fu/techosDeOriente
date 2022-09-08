import { Cliente } from './cliente';
import { Estado } from './estado';
import { DetalleFactura } from './detalle-factura';
import { UsuarioAuxiliar } from './auxiliar/usuario-auxiliar';
import { TipoFactura } from './tipo-factura';
import { Envio } from './envio';

export class Factura {
    idFactura: number;
    noFactura: number;
    total: number;
    fecha: Date;
    serie: string;
    iva: number;
    correlativoSat: string;
    certificacionSat: string;
    serieSat: string;
    mensajeSat: string;
    fechaCertificacionSat: string;

    estado: Estado;
    usuario: UsuarioAuxiliar;
    cliente: Cliente;
    tipoFactura: TipoFactura;
    envio: Envio;
    itemsFactura: DetalleFactura[] = [];

    calcularTotal(): number{
        this.total = 0;
        this.itemsFactura.forEach((item: DetalleFactura) => {
            this.total += item.calcularImporteDescuento();
        });

        return this.total;
    }
}
