import { Producto } from './producto';

export class DetallePedido {
    idDetallePedido: number;
    cantidad: number;
    precioUnitario: number;
    descuento: number;
    precioDescuentoAplicado: number;
    subTotal: number;

    producto: Producto;

    public calcularSubTotal(): number {
        return this.cantidad * this.producto.precioCompra;
    }
}
