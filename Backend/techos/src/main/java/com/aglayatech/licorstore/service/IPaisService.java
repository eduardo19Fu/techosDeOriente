package com.aglayatech.licorstore.service;

import com.aglayatech.licorstore.model.Pais;

import java.util.List;

public interface IPaisService {

    public List<Pais> getAll();

    public Pais getPais(Integer idpais);

    public Pais save(Pais pais);
}
