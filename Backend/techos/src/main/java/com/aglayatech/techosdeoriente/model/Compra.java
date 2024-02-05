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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "compras")
public class Compra implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCompra;
    private String noComprobante;
    private Float iva;
    private BigDecimal totalFlete;
    private BigDecimal totalDescuento;
    private BigDecimal totalCompra;
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

    public Long getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(Long idCompra) {
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

    public void setIva(Float iva) {
        this.iva = iva;
    }

    public BigDecimal getTotalFlete() {
        return totalFlete;
    }

    public void setTotalFlete(BigDecimal totalFlete) {
        this.totalFlete = totalFlete;
    }

    public BigDecimal getTotalDescuento() {
        return totalDescuento;
    }

    public void setTotalDescuento(BigDecimal totalDescuento) {
        this.totalDescuento = totalDescuento;
    }

    public BigDecimal getTotalCompra() {
        return totalCompra;
    }

    public void setTotalCompra(BigDecimal totalCompra) {
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
        final StringBuilder sb = new StringBuilder("Compra{");
        sb.append("idCompra=").append(idCompra);
        sb.append(", noComprobante='").append(noComprobante).append('\'');
        sb.append(", iva=").append(iva);
        sb.append(", totalFlete=").append(totalFlete);
        sb.append(", totalDescuento=").append(totalDescuento);
        sb.append(", totalCompra=").append(totalCompra);
        sb.append(", fechaCompra=").append(fechaCompra);
        sb.append(", fechaRegistro=").append(fechaRegistro);
        sb.append(", proveedor=").append(proveedor);
        sb.append(", estado=").append(estado);
        sb.append(", tipoComprobante=").append(tipoComprobante);
        sb.append(", items=").append(items);
        sb.append(", usuario=").append(usuario);
        sb.append('}');
        return sb.toString();
    }

    private static final long serialVersioUID = 1L;
}
