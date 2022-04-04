import { UsuarioAuxiliar } from './auxiliar/usuario-auxiliar';
import { Cliente } from './cliente';
import { Estado } from './estado';
import { TipoFactura } from './tipo-factura';
import { DetalleProforma } from './detalle-proforma';

export class Proforma {

    idProforma: number;
    noProforma: number;
    total: number;
    fechaEmision: Date;

    estado: Estado;
    usuario: UsuarioAuxiliar;
    cliente: Cliente;
    tipoFactura: TipoFactura;
    itemsProforma: DetalleProforma[] = [];

    calcularTotal(): number{
        this.total = 0;
        this.itemsProforma.forEach((item: DetalleProforma) => {
            this.total += item.calcularImporteDescuento();
        });

        return this.total;
    }
}
