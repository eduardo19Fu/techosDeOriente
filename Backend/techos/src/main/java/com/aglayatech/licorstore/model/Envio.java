package com.aglayatech.licorstore.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "envios")
public class Envio implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEnvio;
    private LocalDate fechaPedido;
    private String telefonoReferencia;
    private Double totalEnvio;
    private Double abono;
    private Double saldoPendiente;
    private LocalDateTime fechaRegistro;
    @NotNull(message = "La referencia no puede estar vacía.")
    private String referencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_envio")
    @JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
    private List<DetalleEnvio> itemsEnvio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado")
    private Estado estado;

    public Envio() {
    }

    @PrePersist
    private void prepersist() {
        this.fechaRegistro = LocalDateTime.now();
    }

    public Integer getIdEnvio() {
        return idEnvio;
    }

    public void setIdEnvio(Integer idEnvio) {
        this.idEnvio = idEnvio;
    }

    public LocalDate getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(LocalDate fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public String getTelefonoReferencia() {
        return telefonoReferencia;
    }

    public void setTelefonoReferencia(String telefonoReferencia) {
        this.telefonoReferencia = telefonoReferencia;
    }

    public Double getTotalEnvio() {
        return totalEnvio;
    }

    public void setTotalEnvio(Double totalEnvio) {
        this.totalEnvio = totalEnvio;
    }

    public Double getAbono() {
        return abono;
    }

    public void setAbono(Double abono) {
        this.abono = abono;
    }

    public Double getSaldoPendiente() {
        return saldoPendiente;
    }

    public void setSaldoPendiente(Double saldoPendiente) {
        this.saldoPendiente = saldoPendiente;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<DetalleEnvio> getItemsEnvio() {
        return itemsEnvio;
    }

    public void setItemsEnvio(List<DetalleEnvio> itemsEnvio) {
        this.itemsEnvio = itemsEnvio;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Envio{" +
                "idEnvio=" + idEnvio +
                ", fechaPedido=" + fechaPedido +
                ", telefonoReferencia='" + telefonoReferencia + '\'' +
                ", totalEnvio=" + totalEnvio +
                ", abono=" + abono +
                ", saldoPendiente=" + saldoPendiente +
                ", fechaRegistro=" + fechaRegistro +
                ", referencia='" + referencia + '\'' +
                ", cliente=" + cliente +
                ", usuario=" + usuario +
                ", itemsEnvio=" + itemsEnvio +
                ", estados=" + estado +
                '}';
    }

    private final static long serialVersionUID = 1L;
}
