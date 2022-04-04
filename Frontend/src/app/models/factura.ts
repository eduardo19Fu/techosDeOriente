import { Cliente } from './cliente';
import { Estado } from './estado';
import { DetalleFactura } from './detalle-factura';
import { UsuarioAuxiliar } from './auxiliar/usuario-auxiliar';
import { TipoFactura } from './tipo-factura';

export class Factura {
    idFactura: number;
    noFactura: number;
    total: number;
    fecha: Date;
    serie: string;

    estado: Estado;
    usuario: UsuarioAuxiliar;
    cliente: Cliente;
    tipoFactura: TipoFactura;
    itemsFactura: DetalleFactura[] = [];

    calcularTotal(): number{
        this.total = 0;
        this.itemsFactura.forEach((item: DetalleFactura) => {
            this.total += item.calcularImporteDescuento();
        });

        return this.total;
    }
}
