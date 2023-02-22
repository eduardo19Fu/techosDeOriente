package com.aglayatech.licorstore.service.impl;

import com.aglayatech.licorstore.model.Envio;
import com.aglayatech.licorstore.repository.IEnvioRepository;
import com.aglayatech.licorstore.service.IEnvioService;
import net.sf.jasperreports.engine.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
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

    protected ByteArrayOutputStream getByteArrayOutputStream(JasperPrint jasperPrint) throws JRException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, byteArrayOutputStream);
        return byteArrayOutputStream;
    }
}
