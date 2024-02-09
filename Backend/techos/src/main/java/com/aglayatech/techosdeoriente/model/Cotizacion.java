package com.aglayatech.techosdeoriente.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "cotizaciones")
public class Cotizacion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCotizacion;
    private BigDecimal total;
    private LocalDateTime fechaEmision;

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
    @JoinColumn(name = "id_cotizacion")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private List<DetalleCotizacion> itemsProforma;

    @PrePersist
    public void prepersist(){
        this.fechaEmision = LocalDateTime.now();
    }

    public Long getIdCotizacion() {
        return idCotizacion;
    }

    public void setIdCotizacion(Long idCotizacion) {
        this.idCotizacion = idCotizacion;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDateTime fechaEmision) {
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
        final StringBuilder sb = new StringBuilder("Cotizacion{");
        sb.append("idCotizacion=").append(idCotizacion);
        sb.append(", total=").append(total);
        sb.append(", fechaEmision=").append(fechaEmision);
        sb.append(", usuario=").append(usuario);
        sb.append(", estado=").append(estado);
        sb.append(", cliente=").append(cliente);
        sb.append(", itemsProforma=").append(itemsProforma);
        sb.append('}');
        return sb.toString();
    }

    private static final long serialVersionUID = 1L;
}
