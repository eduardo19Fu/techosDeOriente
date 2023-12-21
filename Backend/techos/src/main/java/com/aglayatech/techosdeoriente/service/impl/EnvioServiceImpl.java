package com.aglayatech.techosdeoriente.service.impl;

import com.aglayatech.techosdeoriente.model.Envio;
import com.aglayatech.techosdeoriente.repository.IEnvioRepository;
import com.aglayatech.techosdeoriente.service.IEnvioService;
import net.sf.jasperreports.engine.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EnvioServiceImpl implements IEnvioService {

    @Autowired
    private IEnvioRepository envioRepository;

    @Autowired
    private DataSource localDataSource;

    @Override
    @Transactional(readOnly = true)
    public List<Envio> getAll() {
        return envioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Envio getEnvio(Integer id) {
        return envioRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Envio save(Envio envio) {
        return envioRepository.save(envio);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        envioRepository.deleteById(id);
    }

    @Override
    public byte[] showPedido(Integer idenvio) throws SQLException, JRException {

        Connection connection = localDataSource.getConnection();
        Map<String, Object> params = new HashMap<>();
        params.put("idenvio", idenvio);

        InputStream file = getClass().getResourceAsStream("/reports/rpt_envio.jrxml");

        JasperReport jasperReport = JasperCompileManager.compileReport(file);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, connection);

        ByteArrayOutputStream byteArrayOutputStream = getByteArrayOutputStream(jasperPrint);

        connection.close();
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public byte[] rptEnviosRealizados(String fecha) throws SQLException, JRException, ParseException {
        Date fechaFiltro = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Connection connection = localDataSource.getConnection();
        Map params = new HashMap();

        fechaFiltro = format.parse(fecha);
        params.put("fechaPedido", fechaFiltro);

        InputStream file = getClass().getResourceAsStream("/reports/rpt_envios_realizados.jrxml");
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
