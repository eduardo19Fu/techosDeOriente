package com.aglayatech.techosdeoriente.service;

import com.aglayatech.techosdeoriente.model.Pais;

import java.util.List;

public interface IPaisService {

    public List<Pais> getAll();

    public Pais getPais(Integer idpais);

    public Pais save(Pais pais);
}
