package com.aglayatech.techosdeoriente.service;

import com.aglayatech.techosdeoriente.model.Cotizacion;
import net.sf.jasperreports.engine.JRException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.List;

public interface ICotizacionService {

    public List<Cotizacion> findAll();

    public Page<Cotizacion> findAll(Pageable pageable);

    public Cotizacion findProforma(Long idproforma);

    public Cotizacion save(Cotizacion proforma);

    public void delete(Long id);

    /********* PDF REPORTS SERVICES ***********/
    public byte[] showCotizacion(Long idcotizacion) throws JRException, FileNotFoundException, SQLException;

}
