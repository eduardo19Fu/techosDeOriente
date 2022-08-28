package com.aglayatech.licorstore.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "detalle_envio")
public class DetalleEnvio implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDetalleEnvio;
    private double subTotal;
    private int cantidad;

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

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    @Override
    public String toString() {
        return "DetalleEnvio{" +
                "idDetalleEnvio=" + idDetalleEnvio +
                ", subTotal=" + subTotal +
                ", cantidad=" + cantidad +
                ", producto=" + producto +
                '}';
    }

    private static final long serialVersionUID = 1L;
}
