import { Producto } from './producto';

export class DetalleProforma {

    idDetalle: number;
    cantidad = 1;
    subTotal: number;
    descuento: number;
    subTotalDescuento: number;

    producto: Producto;

    public calcularImporte(): number{
        return this.producto.precioVenta * this.cantidad;

        // return this.producto.precioVenta * this.cantidad;
    }

    public calcularImporteDescuento(): number{
        return this.descuento <= 0 ? this.producto.precioVenta * this.cantidad
        : (this.producto.precioVenta * this.cantidad) - ((this.descuento / 100) * (this.producto.precioVenta * this.cantidad));
    }
}
