package com.aglayatech.techosdeoriente.service;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.aglayatech.techosdeoriente.model.TipoMovimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.aglayatech.techosdeoriente.model.MovimientoProducto;
import com.aglayatech.techosdeoriente.model.Producto;

import net.sf.jasperreports.engine.JRException;

public interface IMovimientoProductoService {
	
	public List<MovimientoProducto> findAll();
	
	public List<MovimientoProducto> findByFecha(Date fechaIni, Date fechaFin);
	
	public Page<MovimientoProducto> findAll(Pageable pageble);
	
	public Page<MovimientoProducto> findProductoMoves(Producto producto, Pageable pageable);
	
	public MovimientoProducto save(MovimientoProducto movimientoProducto);

	public List<TipoMovimiento> getTiposMovimiento();
	
	/********* PDF REPORTS SERVICES ***********/
	
	public byte[] inventory(Date fechaIni, Date fechaFin) throws JRException, FileNotFoundException, SQLException;

    public TipoMovimiento findTipoMovimiento(String venta);
}
