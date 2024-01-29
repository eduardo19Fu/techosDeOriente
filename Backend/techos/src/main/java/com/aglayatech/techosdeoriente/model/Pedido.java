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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPedido;
    private BigDecimal totalPedido;
    private LocalDateTime fechaRegistro;
    private LocalDate fechaPedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado")
    @JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
    private Estado estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proveedor")
    @JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
    private Proveedor proveedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    @JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
    private Usuario usuario;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_pedido")
    @JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
    private List<DetallePedido> itemsPedido;

    @PrePersist
    public void prepersist() {
        this.fechaRegistro = LocalDateTime.now();
    }

    public Long getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Long idPedido) {
        this.idPedido = idPedido;
    }

    public BigDecimal getTotalPedido() {
        return totalPedido;
    }

    public void setTotalPedido(BigDecimal totalPedido) {
        this.totalPedido = totalPedido;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public LocalDate getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(LocalDate fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<DetallePedido> getItemsPedido() {
        return itemsPedido;
    }

    public void setItemsPedido(List<DetallePedido> itemsPedido) {
        this.itemsPedido = itemsPedido;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Pedido{");
        sb.append("idPedido=").append(idPedido);
        sb.append(", totalPedido=").append(totalPedido);
        sb.append(", fechaRegistro=").append(fechaRegistro);
        sb.append(", fechaPedido=").append(fechaPedido);
        sb.append(", estado=").append(estado);
        sb.append(", proveedor=").append(proveedor);
        sb.append(", usuario=").append(usuario);
        sb.append(", itemsPedido=").append(itemsPedido);
        sb.append('}');
        return sb.toString();
    }

    private static final long serialVersionUID = 1L;
}
