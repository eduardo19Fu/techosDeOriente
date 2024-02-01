import { Proveedor } from './proveedor';
import { Estado } from './estado';
import { TipoComprobante } from './tipo-comprobante';
import { DetalleCompra } from './detalle-compra';
import { UsuarioAuxiliar } from './auxiliar/usuario-auxiliar';

export class Compra {
    idCompra: number;
    noComprobante: string;
    iva: number;
    totalFlete: number;
    totalDescuento: number;
    totalCompra: number;
    fechaCompra: Date;

    proveedor: Proveedor
    estado: Estado;
    tipoComprobante: TipoComprobante;
    usuario: UsuarioAuxiliar;
    items: DetalleCompra[] = [];

    /**
     * Función que retorna el valor del IVA calculado
     * @return valorIva
     */
    
    calcularIva(): number {
        const valorIva = 0.12;

        return (this.totalCompra * valorIva);
    }

    calcularTotal(): number {
        this.totalCompra = 0;

        if (!this.totalDescuento) {
            this.totalDescuento = 0;
        }

        if (!this.totalFlete) {
            this.totalFlete = 0;
        }

        this.items.forEach(item => {
            this.totalCompra += (item.calcularSubTotal() + this.totalFlete - this.totalDescuento);
        });
        return this.totalCompra;
    }
}
