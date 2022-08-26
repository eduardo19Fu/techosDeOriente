import { Producto } from './producto';

export class DetalleCompra {
    cantidad: number;
    precioUnitario: number;
    subTotal: number;

    producto: Producto;

    public calcularSubTotal(): number {
        return this.cantidad * this.producto.precioCompra;
    }
}
