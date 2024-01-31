package com.aglayatech.techosdeoriente.service;

import com.aglayatech.techosdeoriente.model.Pedido;
import net.sf.jasperreports.engine.JRException;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.List;

public interface IPedidoService {

    public List<Pedido> getAll();

    public Pedido getPedido(Long id);

    public Pedido save(Pedido pedido);

    public void delete(Long id);

    /********* PDF REPORTS SERVICES ***********/
    public byte[] showReport(Long idpedido) throws JRException, FileNotFoundException, SQLException;


}
