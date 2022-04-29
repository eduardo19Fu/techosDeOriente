import { Estado } from './estado';
import { MarcaProducto } from './marca-producto';
import { TipoProducto } from './tipo-producto';

export class Producto {
    idProducto: number;
    codProducto: string;
    nombre: string;
    precioCompra: number;
    precioVenta: number;
    porcentajeGanancia: number;
    fechaVencimiento: Date;
    fechaIngreso: Date;
    fechaRegistro: Date;
    stock: number;
    imagen: string;
    descripcion: string;
    link: string;

    tipoProducto: TipoProducto;
    marcaProducto: MarcaProducto;
    estado: Estado;

    generarCodigo(): string{
        var rand: number;
        rand = Math.random() * (5000 - 1) + 1; // DEVUELVE UN VALOR ALEATORIO ENTRE 1 Y 5000
        return rand + this.nombre.substring(0,1);
    }

}
