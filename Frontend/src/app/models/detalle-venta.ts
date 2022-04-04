import { Producto } from './producto';
import { Venta } from './venta';

export class DetalleVenta {
    private idDetalle: number;
    private subTotal: number;

    private producto: Producto;
    private venta: Venta;

    constructor(){}

    public getIdDetalle(): number{
        return this.idDetalle;
    }

    // tslint:disable-next-line: typedef
    public setIdDetalle(idDetalle: number){
        this.idDetalle = idDetalle;
    }

    public getSubTotal(): number{
        return this.subTotal;
    }

    // tslint:disable-next-line: typedef
    public setSubTotal(subTotal: number){
        this.subTotal = subTotal;
    }

    public getProducto(): Producto{
        return this.producto;
    }

    // tslint:disable-next-line: typedef
    public setProducto(producto: Producto){
        this.producto = producto;
    }

    public getVenta(): Venta{
        return this.venta;
    }

    // tslint:disable-next-line: typedef
    public setVenta(venta: Venta){
        this.venta = venta;
    }
}
