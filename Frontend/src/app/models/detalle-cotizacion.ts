import { Producto } from "./producto";

export class DetalleCotizacion {
    idCotizacionDetalle: number;
    cantidad: number;
    descuento: number;
    subTotal: number;
    subTotalDescuento: number;

    producto: Producto;

    public calcularImporte(): number{
        return this.producto.precioVenta * this.cantidad;
    }

    /** Metodo que calcula el nuevo precio venta con o sin descuento aplicado
     * @returns Valor del precioVenta por item
     */
    public calcularNuevoPrecioVenta(): number {
        if (this.descuento <= 0) {
            return parseFloat(this.producto.precioVenta.toFixed(2));
        } else {
            const precioConDescuento = this.producto.precioVenta - (this.descuento / 100) * this.producto.precioVenta;
            return parseFloat(precioConDescuento.toFixed(2));
        }
    }

    /**
     * Método que calcula el subTotal del item en caso de poseer o no descuento.
     * @returns subTotal con descuento aplicado
     */
    public calcularImporteDescuento(): number {
        const importeSinDescuento = this.producto.precioVenta * this.cantidad;
        if (this.descuento <= 0) {
            return parseFloat(importeSinDescuento.toFixed(2));
        } else {
            const importeConDescuento = this.calcularNuevoPrecioVenta() * this.cantidad;
            return parseFloat(importeConDescuento.toFixed(2));
        }
    }
}
