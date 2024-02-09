package com.aglayatech.techosdeoriente.service.impl;

import com.aglayatech.techosdeoriente.model.Cotizacion;
import com.aglayatech.techosdeoriente.repository.ICotizacionRepository;
import com.aglayatech.techosdeoriente.service.ICotizacionService;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CotizacionServiceImpl implements ICotizacionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FacturaServiceImpl.class);

    @Autowired
    private ICotizacionRepository proformaRepository;

    @Autowired
    protected DataSource localDataSource;

    @Override
    public List<Cotizacion> findAll() {
        return this.proformaRepository.findAll(Sort.by(Direction.DESC, "fechaEmision"));
    }

    @Override
    public Page<Cotizacion> findAll(Pageable pageable) {
        return this.proformaRepository.findAll(pageable);
    }

    @Override
    public Cotizacion findProforma(Long idproforma) {
        return this.proformaRepository.findById(idproforma).orElse(null);
    }

    @Override
    public Cotizacion save(Cotizacion proforma) {
        return this.proformaRepository.save(proforma);
    }

    @Override
    public void delete(Long id) {
        proformaRepository.deleteById(id);
    }

    /********* PDF REPORTS SERVICES
     * @param idcotizacion***********/
    @Override
    public byte[] showCotizacion(Long idcotizacion) throws JRException, FileNotFoundException, SQLException {
        Connection con = localDataSource.getConnection();
        Map<String, Object> params = new HashMap<>();
        params.put("ID", idcotizacion);
        InputStream file = getClass().getResourceAsStream("/reports/cotizacion.jrxml");

        JasperReport jasperReport = JasperCompileManager.compileReport(file);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, con);

        ByteArrayOutputStream byteArrayOutputStream = getByteArrayOutputStream(jasperPrint);

        con.close();
        return byteArrayOutputStream.toByteArray();
    }

    protected ByteArrayOutputStream getByteArrayOutputStream(JasperPrint jasperPrint) throws JRException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, byteArrayOutputStream);
        return byteArrayOutputStream;
    }
}
