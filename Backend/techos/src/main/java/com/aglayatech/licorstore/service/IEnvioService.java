package com.aglayatech.licorstore.service;

import com.aglayatech.licorstore.model.Envio;

import java.util.List;

public interface IEnvioService {

    List<Envio> getAll();

    Envio getEnvio(Integer id);

    Envio save(Envio envio);

    void delete(Integer id);
}
