package com.aglayatech.techosdeoriente.service;

import com.aglayatech.techosdeoriente.model.Envio;
import net.sf.jasperreports.engine.JRException;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

public interface IEnvioService {

    List<Envio> getAll();

    Envio getEnvio(Integer id);

    Envio save(Envio envio);

    void delete(Integer id);

    /********** SERVICIOS DE REPORTES ***********/

    public byte[] showPedido(Integer idenvio) throws SQLException, JRException;

    public byte[] rptEnviosRealizados(String fecha) throws SQLException, JRException, ParseException;


}
