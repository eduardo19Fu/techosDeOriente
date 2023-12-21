package com.aglayatech.techosdeoriente.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import javax.sql.DataSource;

import com.aglayatech.techosdeoriente.model.TipoMovimiento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.aglayatech.techosdeoriente.model.MovimientoProducto;
import com.aglayatech.techosdeoriente.model.Producto;
import com.aglayatech.techosdeoriente.repository.IMovimientoProductoRepository;
import com.aglayatech.techosdeoriente.service.IMovimientoProductoService;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@Service
public class MovimientoProductoServiceImpl implements IMovimientoProductoService {

	@Autowired
	private IMovimientoProductoRepository repoMovimiento;
	
	@Autowired
	private DataSource localDateSource;
	
	@Override
	public List<MovimientoProducto> findAll() {
		return repoMovimiento.findAll();
	}

	@Override
	public Page<MovimientoProducto> findAll(Pageable pageble) {
		return repoMovimiento.findAll(pageble);
	}

	@Override
	public Page<MovimientoProducto> findProductoMoves(Producto producto, Pageable pageable) {
		return repoMovimiento.findByProducto(producto, pageable);
	}

	@Override
	public MovimientoProducto save(MovimientoProducto movimientoProducto) {
		return repoMovimiento.save(movimientoProducto);
	}

	@Override
	public List<TipoMovimiento> getTiposMovimiento() {
		return this.repoMovimiento.findTiposMovimiento();
	}

	@Override
	public List<MovimientoProducto> findByFecha(Date fechaIni, Date fechaFin) {
		return repoMovimiento.findByFechaMovimientoBetween(fechaIni, fechaFin);
	}

	/********* PDF REPORTS SERVICES ***********/
	
	@Override
	public byte[] inventory(Date fechaIni, Date fechaFin) 
			throws JRException, FileNotFoundException, SQLException { // REPORTE DE INVENTARIO
		
		Connection con = localDateSource.getConnection();
		Map<String, Object> params = new HashMap<>();
		InputStream file = getClass().getResourceAsStream("/reports/rpt_inventario.jrxml");

		params.put("fechaIni", fechaIni);
		params.put("fechaFin", fechaFin);
		
		JasperReport jasperReport = JasperCompileManager.compileReport(file);
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, con);
		
		ByteArrayOutputStream byteArrayOutputStream = getByteArrayOutputStream(jasperPrint);
		
		con.close();
		return byteArrayOutputStream.toByteArray();
	}

	@Override
	public TipoMovimiento findTipoMovimiento(String tipoMovimiento) {
		Optional<TipoMovimiento> optional = this.repoMovimiento.findTipoMovimientoByNombre(tipoMovimiento);
		return (optional.isPresent() ? optional.get() : null);
	}

	protected ByteArrayOutputStream getByteArrayOutputStream(JasperPrint jasperPrint) throws JRException {
	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    JasperExportManager.exportReportToPdfStream(jasperPrint, byteArrayOutputStream);
	    return byteArrayOutputStream;
	}

}
