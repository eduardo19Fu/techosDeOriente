package com.aglayatech.licorstore.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "compras")
public class Compra implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCompra;
    private String noComprobante;
    private float iva;
    private Double totalCompra;

    @Temporal(TemporalType.DATE)
    private Date fechaCompra;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaRegistro;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_compra")
    @JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
    private List<DetalleCompra> items;

    @PrePersist
    public void prepersist() {
        this.fechaRegistro = new Date();
    }

    public Integer getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(Integer idCompra) {
        this.idCompra = idCompra;
    }

    public Date getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(Date fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public String getNoComprobante() {
        return noComprobante;
    }

    public void setNoComprobante(String noComprobante) {
        this.noComprobante = noComprobante;
    }

    public float getIva() {
        return iva;
    }

    public void setIva(float iva) {
        this.iva = iva;
    }

    public Double getTotalCompra() {
        return totalCompra;
    }

    public void setTotalCompra(Double totalCompra) {
        this.totalCompra = totalCompra;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public List<DetalleCompra> getItems() {
        return items;
    }

    public void setItems(List<DetalleCompra> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Compra{" +
                "idCompra=" + idCompra +
                ", noComprobante='" + noComprobante + '\'' +
                ", iva=" + iva +
                ", totalCompra=" + totalCompra +
                ", fechaCompra=" + fechaCompra +
                ", fechaRegistro=" + fechaRegistro +
                ", items=" + items +
                '}';
    }

    private static final long serialVersioUID = 1L;
}
