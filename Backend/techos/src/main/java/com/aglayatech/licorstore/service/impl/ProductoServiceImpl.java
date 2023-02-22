package com.aglayatech.licorstore.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.aglayatech.licorstore.model.Estado;
import com.aglayatech.licorstore.model.Producto;
import com.aglayatech.licorstore.repository.IProductoRepository;
import com.aglayatech.licorstore.service.IProductoService;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@SuppressWarnings("unused")
@Service
public class ProductoServiceImpl implements IProductoService {

	private final static Logger LOGGER = LoggerFactory.getLogger(ProductoServiceImpl.class);

	@Autowired
	private IProductoRepository repoProducto;
	
	@Autowired
	protected DataSource localDataSource;

	@Override
	public List<Producto> findAll() {
		return repoProducto.findAll(Sort.by(Direction.ASC, "nombre"));
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
	public List<Producto> findCaducados() {
		return repoProducto.findCaducados(new Date());
	}
	
	@Override
	public List<Producto> findAllByEstado(Estado estado) {
		return repoProducto.findByEstado(estado);
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
		LOGGER.debug("Parámetro fecha mapeado: {}", params.get("pFecha").toString());
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
