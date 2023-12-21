package com.aglayatech.techosdeoriente.service;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import com.aglayatech.techosdeoriente.model.TipoFactura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.aglayatech.techosdeoriente.model.Factura;

import net.sf.jasperreports.engine.JRException;

public interface IFacturaService {
	
	public List<Factura> findAll();
	
	public Page<Factura> findAll(Pageable pageable);
	
	public Factura findFactura(Long idfactura);
	
	public Factura save(Factura factura);

	public TipoFactura findTipoFactura(Integer idTipoFactura);

	public Integer getMaxVentas();
	
	/********* PDF REPORTS SERVICES ***********/
	
	public byte[] resportDailySales(Integer usuario, Date fecha) throws JRException, FileNotFoundException, SQLException;
	
	public byte[] showBill(Long idfactura) throws JRException, FileNotFoundException, SQLException;

	public byte[] showBill2(Long idfactura) throws JRException, FileNotFoundException, SQLException;

	public byte[] reportMonthlySales(Integer year) throws JRException, FileNotFoundException, SQLException;

	public byte[] reportAllDailySales(String fecha) throws JRException, FileNotFoundException, SQLException, ParseException;

}
