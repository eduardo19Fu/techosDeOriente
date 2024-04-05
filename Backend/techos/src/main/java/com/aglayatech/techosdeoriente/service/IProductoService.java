package com.aglayatech.techosdeoriente.service;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.aglayatech.techosdeoriente.model.Estado;
import com.aglayatech.techosdeoriente.model.Producto;

import net.sf.jasperreports.engine.JRException;

public interface IProductoService {
	
	public List<Producto> findAll();

	public List<Producto> findAllBySP();
	
	public List<Producto> findAllByEstado(Estado estado);
	
	public Page<Producto> findAll(Pageable pageable);
	
	public Producto findById(Integer idproducto);
	
	public Producto save(Producto producto);
	
	public void delete(Integer idproducto);

	public Integer getMaxProductos();
	
	// Busqueda de Productos desde el frontend
	public List<Producto> findByName(String name);
	
	public Producto findByCodigo(String codigo);

	public Producto findBySerie(String serie);
	
	// Listado de productos caducados
	public List<Producto> findCaducados();
	
	// SERVICIOS DE REPORTES
	public String reportExpired() throws JRException, FileNotFoundException, SQLException;

	public byte[] reportInventory(String fecha) throws JRException, FileNotFoundException, SQLException, ParseException;

}
