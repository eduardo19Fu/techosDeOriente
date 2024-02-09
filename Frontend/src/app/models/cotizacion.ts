import { UsuarioAuxiliar } from "./auxiliar/usuario-auxiliar";
import { Cliente } from "./cliente";
import { DetalleCotizacion } from "./detalle-cotizacion";
import { Estado } from "./estado";

export class Cotizacion {
    idCotizacion: number;
    total: number;
    fechaEmision: Date;

    usuario: UsuarioAuxiliar;
    estado: Estado;
    cliente: Cliente;
    itemsProforma: DetalleCotizacion[] = [];

    calcularTotal(): number{
        this.total = 0;
        this.itemsProforma.forEach((item: DetalleCotizacion) => {
            this.total += item.calcularImporteDescuento();
        });

        return this.total;
    }

    generarNoProforma(): string {
        let min = 0;
        let max = 100000;
        let noProforma = Math.floor(Math.random() * (max - min + 1) + min) + 'P';
        return noProforma;
    }
}
