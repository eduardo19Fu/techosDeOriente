package com.aglayatech.techosdeoriente.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
@Table(name = "productos")
public class Producto implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idProducto;
	private String codProducto;
	private String serie;
	private String nombre;
	private Double precioCompra;
	private Double precioVenta;
	private Double precioSugerido;
	private float porcentajeGanancia;
	private String imagen;
	private String descripcion;
	private String link;

	@Temporal(TemporalType.DATE)
	private Date fechaVencimiento;

	@Temporal(TemporalType.DATE)
	private Date fechaIngreso;

	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaRegistro;

	private int stock;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_marca_producto")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private MarcaProducto marcaProducto;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_producto")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private TipoProducto tipoProducto;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_estado")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private Estado estado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_proveedor")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private Proveedor proveedor;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "producto")
	@JsonIgnoreProperties({ "producto", "hibernateLazyInitializer", "handler" })
	private List<MovimientoProducto> movimientos;

	public Producto() {
		this.movimientos = new ArrayList<>();
	}

	@PrePersist
	public void configFechaRegistro() {
		this.fechaRegistro = new Date();
	}

	public Integer getIdProducto() {
		return idProducto;
	}

	public void setIdProducto(Integer idProducto) {
		this.idProducto = idProducto;
	}

	public String getCodProducto() {
		return codProducto;
	}

	public void setCodProducto(String codProducto) {
		this.codProducto = codProducto;
	}

	public String getSerie() {
		return serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Double getPrecioCompra() {
		return precioCompra;
	}

	public void setPrecioCompra(Double precioCompra) {
		this.precioCompra = precioCompra;
	}

	public Double getPrecioVenta() {
		return precioVenta;
	}

	public void setPrecioVenta(Double precioVenta) {
		this.precioVenta = precioVenta;
	}

	public Double getPrecioSugerido() {
		return precioSugerido;
	}

	public void setPrecioSugerido(Double precioSugerido) {
		this.precioSugerido = precioSugerido;
	}

	public float getPorcentajeGanancia() {
		return porcentajeGanancia;
	}

	public void setPorcentajeGanancia(float porcentajeGanancia) {
		this.porcentajeGanancia = porcentajeGanancia;
	}

	public Date getFechaVencimiento() {
		return fechaVencimiento;
	}

	public void setFechaVencimiento(Date fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public MarcaProducto getMarcaProducto() {
		return marcaProducto;
	}

	public void setMarcaProducto(MarcaProducto marcaProducto) {
		this.marcaProducto = marcaProducto;
	}

	public TipoProducto getTipoProducto() {
		return tipoProducto;
	}

	public void setTipoProducto(TipoProducto tipoProducto) {
		this.tipoProducto = tipoProducto;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public String getImagen() {
		return imagen;
	}

	public void setImagen(String imagen) {
		this.imagen = imagen;
	}

	public Date getFechaIngreso() {
		return fechaIngreso;
	}

	public void setFechaIngreso(Date fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}

	public Date getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	public Proveedor getProveedor() {
		return proveedor;
	}

	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}

	public List<MovimientoProducto> getMovimientos() {
		return movimientos;
	}

	public void setMovimientos(List<MovimientoProducto> movimientos) {
		this.movimientos = movimientos;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Producto{");
		sb.append("idProducto=").append(idProducto);
		sb.append(", codProducto='").append(codProducto).append('\'');
		sb.append(", serie='").append(serie).append('\'');
		sb.append(", nombre='").append(nombre).append('\'');
		sb.append(", precioCompra=").append(precioCompra);
		sb.append(", precioVenta=").append(precioVenta);
		sb.append(", precioSugerido=").append(precioSugerido);
		sb.append(", porcentajeGanancia=").append(porcentajeGanancia);
		sb.append(", imagen='").append(imagen).append('\'');
		sb.append(", descripcion='").append(descripcion).append('\'');
		sb.append(", link='").append(link).append('\'');
		sb.append(", fechaVencimiento=").append(fechaVencimiento);
		sb.append(", fechaIngreso=").append(fechaIngreso);
		sb.append(", fechaRegistro=").append(fechaRegistro);
		sb.append(", stock=").append(stock);
		sb.append(", marcaProducto=").append(marcaProducto);
		sb.append(", tipoProducto=").append(tipoProducto);
		sb.append(", estado=").append(estado);
		sb.append(", proveedor=").append(proveedor);
		sb.append(", movimientos=").append(movimientos);
		sb.append('}');
		return sb.toString();
	}

	private static final long serialVersionUID = 1L;

}
