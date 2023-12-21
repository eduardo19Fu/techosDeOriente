package com.aglayatech.techosdeoriente.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "detalle_envio")
public class DetalleEnvio implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDetalleEnvio;
    private double subTotal;
    @NotNull(message = "Cantidad no puede ser 0.")
    private int cantidad;
    private float descuento;
    private double subTotalDescuento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto")
    @JsonIgnoreProperties(value = {"movimientos", "hibernateLazyInitializer", "handler"})
    private Producto producto;

    public Integer getIdDetalleEnvio() {
        return idDetalleEnvio;
    }

    public void setIdDetalleEnvio(Integer idDetalleEnvio) {
        this.idDetalleEnvio = idDetalleEnvio;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public float getDescuento() {
        return descuento;
    }

    public void setDescuento(float descuento) {
        this.descuento = descuento;
    }

    public double getSubTotalDescuento() {
        return subTotalDescuento;
    }

    public void setSubTotalDescuento(double subTotalDescuento) {
        this.subTotalDescuento = subTotalDescuento;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DetalleEnvio{");
        sb.append("idDetalleEnvio=").append(idDetalleEnvio);
        sb.append(", subTotal=").append(subTotal);
        sb.append(", cantidad=").append(cantidad);
        sb.append(", descuento=").append(descuento);
        sb.append(", subTotalDescuento=").append(subTotalDescuento);
        sb.append(", producto=").append(producto);
        sb.append('}');
        return sb.toString();
    }

    private static final long serialVersionUID = 1L;
}
