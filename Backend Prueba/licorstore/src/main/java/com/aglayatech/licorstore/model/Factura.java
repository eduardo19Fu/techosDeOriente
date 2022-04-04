package com.aglayatech.licorstore.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "facturas")
public class Factura implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idFactura;
	private Long noFactura;
	private String serie;
	private Double total;
	private Double iva;

	@Temporal(TemporalType.TIMESTAMP)
	private Date fecha;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_estado")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private Estado estado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private Usuario usuario;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_cliente")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private Cliente cliente;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "id_factura")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private List<DetalleFactura> itemsFactura;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_factura")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private TipoFactura tipoFactura;

	public Factura() {
		itemsFactura = new ArrayList<>();
	}

	@PrePersist
	public void initFecha() {
		this.fecha = new Date();
	}

	public Long getIdFactura() {
		return idFactura;
	}

	public void setIdFactura(Long idFactura) {
		this.idFactura = idFactura;
	}

	public Long getNoFactura() {
		return noFactura;
	}

	public void setNoFactura(Long noFactura) {
		this.noFactura = noFactura;
	}

	public String getSerie() {
		return serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public Double getIva() {
		return iva;
	}

	public void setIva(Double iva) {
		this.iva = iva;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public List<DetalleFactura> getItemsFactura() {
		return itemsFactura;
	}

	public void setItemsFactura(List<DetalleFactura> itemsFactura) {
		this.itemsFactura = itemsFactura;
	}

	public TipoFactura getTipoFactura() {
		return tipoFactura;
	}

	public void setTipoFactura(TipoFactura tipoFactura) {
		this.tipoFactura = tipoFactura;
	}

	public Double calcularTotal() {
		Double total = 0.00;

		for (DetalleFactura item : itemsFactura) {
			total += item.getSubTotal();
		}

		return total;
	}

	public Double calcularIva(){
		float valorIva = (float) 0.12;
		return this.total * valorIva;
	}

	@Override
	public String toString() {
		return "Factura{" +
				"idFactura=" + idFactura +
				", noFactura=" + noFactura +
				", serie='" + serie + '\'' +
				", total=" + total +
				", fecha=" + fecha +
				", estado=" + estado +
				", usuario=" + usuario +
				", cliente=" + cliente +
				", itemsFactura=" + itemsFactura +
				", tipoFactura=" + tipoFactura +
				'}';
	}

	private static final long serialVersionUID = 1L;

}
