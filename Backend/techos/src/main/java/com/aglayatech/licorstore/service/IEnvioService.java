package com.aglayatech.licorstore.service;

import com.aglayatech.licorstore.model.Envio;
import net.sf.jasperreports.engine.JRException;

import java.sql.SQLException;
import java.util.List;

public interface IEnvioService {

    List<Envio> getAll();

    Envio getEnvio(Integer id);

    Envio save(Envio envio);

    void delete(Integer id);

    /********** SERVICIOS DE REPORTES ***********/

    public byte[] showPedido(Integer idenvio) throws SQLException, JRException;


}
