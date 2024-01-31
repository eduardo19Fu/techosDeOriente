package com.aglayatech.techosdeoriente.service.impl;

import com.aglayatech.techosdeoriente.model.Pedido;
import com.aglayatech.techosdeoriente.repository.IPedidoRepository;
import com.aglayatech.techosdeoriente.service.IPedidoService;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class PedidoServiceImpl implements IPedidoService {

    private final static Logger LOGGER = LoggerFactory.getLogger(PedidoServiceImpl.class);

    @Autowired
    private IPedidoRepository pedidoRepository;

    @Autowired
    protected DataSource localDataSource;

    @Override
    public List<Pedido> getAll() {
        return pedidoRepository.findAll();
    }

    @Override
    public Pedido getPedido(Long id) {
        return pedidoRepository.findById(id).orElse(null);
    }

    @Override
    public Pedido save(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    @Override
    public void delete(Long id) {
        pedidoRepository.deleteById(id);
    }

    /********* PDF REPORTS SERVICES
     * @param idpedido***********/
    @Override
    public byte[] showReport(Long idpedido) throws JRException, FileNotFoundException, SQLException {
        Connection con = localDataSource.getConnection();
        Map<String, Object> params = new HashMap<>();
        params.put("idpedido", idpedido);
        InputStream file = getClass().getResourceAsStream("/reports/rpt_pedido_compra.jrxml");

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
