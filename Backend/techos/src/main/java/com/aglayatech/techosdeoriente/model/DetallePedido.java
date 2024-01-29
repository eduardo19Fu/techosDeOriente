package com.aglayatech.techosdeoriente.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle_pedidos")
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDetallePedido;
    private int cantidad;
    private BigDecimal precioUnitario;
    private Float descuento;
    private BigDecimal precioDescuentoAplicado;
    private BigDecimal subTotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto")
    @JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
    private Producto producto;

    public Long getIdDetallePedido() {
        return idDetallePedido;
    }

    public void setIdDetallePedido(Long idDetallePedido) {
        this.idDetallePedido = idDetallePedido;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Float getDescuento() {
        return descuento;
    }

    public void setDescuento(Float descuento) {
        this.descuento = descuento;
    }

    public BigDecimal getPrecioDescuentoAplicado() {
        return precioDescuentoAplicado;
    }

    public void setPrecioDescuentoAplicado(BigDecimal precioDescuentoAplicado) {
        this.precioDescuentoAplicado = precioDescuentoAplicado;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DetallePedido{");
        sb.append("idDetallePedido=").append(idDetallePedido);
        sb.append(", cantidad=").append(cantidad);
        sb.append(", precioUnitario=").append(precioUnitario);
        sb.append(", descuento=").append(descuento);
        sb.append(", precioDescuentoAplicado=").append(precioDescuentoAplicado);
        sb.append(", subTotal=").append(subTotal);
        sb.append(", producto=").append(producto);
        sb.append('}');
        return sb.toString();
    }
}
