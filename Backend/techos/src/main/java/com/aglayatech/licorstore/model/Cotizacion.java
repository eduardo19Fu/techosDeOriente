package com.aglayatech.licorstore.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "proformas")
public class Cotizacion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idproforma;
    private Long noProforma;
    private Double total;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaEmision;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Estado estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Cliente cliente;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_proforma")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private List<DetalleCotizacion> itemsProforma;

    @PrePersist
    public void prepersist(){
        this.fechaEmision = new Date();
    }

    public Long getIdproforma() {
        return idproforma;
    }

    public void setIdproforma(Long idproforma) {
        this.idproforma = idproforma;
    }

    public Long getNoProforma() {
        return noProforma;
    }

    public void setNoProforma(Long noProforma) {
        this.noProforma = noProforma;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<DetalleCotizacion> getItemsProforma() {
        return itemsProforma;
    }

    public void setItemsProforma(List<DetalleCotizacion> itemsProforma) {
        this.itemsProforma = itemsProforma;
    }

    @Override
    public String toString() {
        return "Proforma{" +
                "idproforma=" + idproforma +
                ", noProforma=" + noProforma +
                ", total=" + total +
                ", fechaEmision=" + fechaEmision +
                ", usuario=" + usuario +
                ", estado=" + estado +
                ", cliente=" + cliente +
                ", itemsProforma=" + itemsProforma +
                '}';
    }

    private static final long serialVersionUID = 1L;
}
