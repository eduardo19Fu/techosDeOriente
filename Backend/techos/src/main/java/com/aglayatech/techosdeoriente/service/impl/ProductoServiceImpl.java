package com.aglayatech.techosdeoriente.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.aglayatech.techosdeoriente.model.DetalleCompra;
import com.aglayatech.techosdeoriente.service.IEstadoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.aglayatech.techosdeoriente.model.Estado;
import com.aglayatech.techosdeoriente.model.Producto;
import com.aglayatech.techosdeoriente.repository.IProductoRepository;
import com.aglayatech.techosdeoriente.service.IProductoService;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@Service
public class ProductoServiceImpl implements IProductoService {

	private final static Logger LOGGER = LoggerFactory.getLogger(ProductoServiceImpl.class);

	@Autowired
	private IProductoRepository repoProducto;

	@Autowired
	private IEstadoService estadoService;
	
	@Autowired
	protected DataSource localDataSource;

	@Override
	public List<Producto> findAll() {
		return repoProducto.findAll(Sort.by(Direction.ASC, "nombre"));
	}

	@Override
	public List<Producto> findAllBySP() {
		return repoProducto.getAllProductosSP();
	}

	@Override
	public Page<Producto> findAll(Pageable pageable) {
		return repoProducto.findAll(pageable);
	}

	
	@Override
	public Producto findById(Integer idproducto) {
		return repoProducto.findById(idproducto).orElse(null);
	}

	@Override
	public Producto save(Producto producto) {
		return repoProducto.save(producto);
	}

	@Override
	public void delete(Integer idproducto) {
		repoProducto.deleteById(idproducto);
	}

	@Override
	public Integer getMaxProductos() {
		return repoProducto.getMaxProductos();
	}

	// Implementaciones para busqueda desde el frontend
	@Override
	public List<Producto> findByName(String name) {
		return repoProducto.findByNombreContaining(name);
	}

	@Override
	public Producto findByCodigo(String codigo) {
		return repoProducto.findByCodigo(codigo).orElse(null);
	}

	@Override
	public Producto findBySerie(String serie) {
		return repoProducto.findBySerie(serie).orElse(null);
	}

	@Override
	public List<Producto> findCaducados() {
		return repoProducto.findCaducados(new Date());
	}
	
	@Override
	public List<Producto> findAllByEstado(Estado estado) {
		return repoProducto.findByEstado(estado);
	}

	/**
	 * Método que recibe un producto que no existe y lo registra.
	 *
	 * @param item Recibe el item a analizar.
	 * @param fechaIngreso Fecha de ingreso para el producto misma que la compra realizada
	 * @return boolean Devuelve un valor booleano analizando si se llevo a cabo el registro con éxito o no.
	 * @exception DataAccessException Lanza una posible excepcion en caso de ocurrir un problema a nivel de base de datos del tipo
	 *
	 * */
	public boolean crearProducto(DetalleCompra item, LocalDate fechaIngreso, String tipoMovimiento) {
		Producto producto = null;
		Estado estadoProductoNuevo = null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

		try {
			estadoProductoNuevo = this.estadoService.findByEstado("activo".toUpperCase());
			producto = findByCodigo(item.getProducto().getCodProducto());

			if(producto != null) {
				producto.setPrecioCompra(item.getProducto().getPrecioCompra());
				producto.setPrecioVenta(item.getProducto().getPrecioVenta());
				producto.setPrecioSugerido(item.getProducto().getPrecioSugerido());
				producto.setPorcentajeGanancia(item.getProducto().getPorcentajeGanancia());
				this.updateExistencias(producto, item.getCantidad(), tipoMovimiento.toUpperCase());
			} else if (producto == null) {
				producto = item.getProducto();
				producto.setEstado(estadoProductoNuevo);
				producto.setFechaIngreso(simpleDateFormat.parse(fechaIngreso.toString()));
			}

			save(producto);
			return true;
		} catch(DataAccessException | ParseException e) {
			LOGGER.error(e.getMessage());
			return false;
		}
	}

	/**
	 * Función encargado de la actualización de existencias.
	 * <p>Este se encarga de determinar la cantidad de existencias del  producto recién ingresado
	 * para poder actualizar su stock de forma adecuada según los items recibidos y según el tipo de movimiento.</p>
	 * <p>Si el tipo de movimiento es compra, agregará la cantidad recibida al stock del producto.</p>
	 * <p>Si el tipo de movimiento es eliminar_compra, restará la cantidad agregada anteriormente al stock.</p>
	 * @param producto Es el producto a procesar
	 * @param cantidad Es la cantidad a agregar o quitar
	 * @param tipoMovimiento Determina el tipo movimiento que se va a realizar
	 * */
	public void updateExistencias(Producto producto, int cantidad, String tipoMovimiento) {
		Producto productoUpdated = new Producto();

		switch (tipoMovimiento) {
			case "COMPRA":
			case "ANULACION_FACTURA":
				producto.setStock(producto.getStock() + cantidad);
				break;
			case "ELIMINAR_COMPRA":
			case "VENTA":
				producto.setStock(producto.getStock() - cantidad);
				break;
			default:
				LOGGER.error("No existe una operación asignada para el tipo de movimiento ".concat(tipoMovimiento));
				break;
		}
		productoUpdated = save(producto);
	}


	/************** SERVICIOS PARA REPORTES **************/
	
	@Override
	public String reportExpired() throws JRException, FileNotFoundException, SQLException { // REPORTE DE PRODUCTOS CADUCADOS
		String path = "C:\\Users\\Edfu-pro\\desktop";
		// List<Producto> productos = this.findCaducados();
		Connection con = localDataSource.getConnection(); // Obtiene la conexión actual a la base de datos
		File file = ResourceUtils.getFile("classpath:\\reports\\rpt_productos_caducados.jrxml");
		JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
		// Puebla EL REPORTE con un listado de productos obtenidos con una consulta jpa
		// JRBeanCollectionDataSource datasource = new JRBeanCollectionDataSource(productos);
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, con);
		con.close();
		JasperExportManager.exportReportToPdfFile(jasperPrint, path+"\\productos_caducados.pdf");
		return "Reporte Creado";
	}

	@Override
	public byte[] reportInventory(String fecha) throws JRException, FileNotFoundException, SQLException, ParseException {

		Date parametroFecha = null;
		final Connection connection = localDataSource.getConnection();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Map<String, Object> params = new HashMap<>();

		parametroFecha = format.parse(fecha);
		params.put("pFecha", parametroFecha);
		LOGGER.info("Parámetro fecha mapeado: {}", params.get("pFecha").toString());
		InputStream file = getClass().getResourceAsStream("/reports/rpt_inventario.jrxml");

		JasperReport jasperReport = JasperCompileManager.compileReport(file);
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, connection);
		ByteArrayOutputStream byteArrayOutputStream = getByteArrayOutputStream(jasperPrint);
		connection.close();

		return byteArrayOutputStream.toByteArray();
	}

	protected ByteArrayOutputStream getByteArrayOutputStream(JasperPrint jasperPrint) throws JRException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		JasperExportManager.exportReportToPdfStream(jasperPrint, byteArrayOutputStream);
		return byteArrayOutputStream;
	}
}
