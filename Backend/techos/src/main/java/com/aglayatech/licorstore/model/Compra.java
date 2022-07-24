package com.aglayatech.licorstore.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private LocalDate fechaCompra;
    private LocalDateTime fechaRegistro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proveedor")
    @JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
    private Proveedor proveedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado")
    @JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
    private Estado estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_comprobante")
    @JsonIgnoreProperties(value ={ "hibernateLazyInitializer", "handler" })
    private TipoComprobante tipoComprobante;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_compra")
    @JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
    private List<DetalleCompra> items;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Usuario usuario;

    public Compra() {
        this.items = new ArrayList<>();
    }

    @PrePersist
    public void prepersist() {
        this.fechaRegistro = LocalDateTime.now();
    }

    public Integer getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(Integer idCompra) {
        this.idCompra = idCompra;
    }

    public LocalDate getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(LocalDate fechaCompra) {
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

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public List<DetalleCompra> getItems() {
        return items;
    }

    public void setItems(List<DetalleCompra> items) {
        this.items = items;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public TipoComprobante getTipoComprobante() {
        return tipoComprobante;
    }

    public void setTipoComprobante(TipoComprobante tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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
                ", proveedor=" + proveedor +
                ", estado=" + estado +
                ", tipoComprobante=" + tipoComprobante +
                ", items=" + items +
                '}';
    }

    private static final long serialVersioUID = 1L;
}
