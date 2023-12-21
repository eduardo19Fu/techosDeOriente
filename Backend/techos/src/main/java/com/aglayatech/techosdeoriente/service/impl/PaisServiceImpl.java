package com.aglayatech.techosdeoriente.service.impl;

import com.aglayatech.techosdeoriente.model.Pais;
import com.aglayatech.techosdeoriente.repository.IPaisRepository;
import com.aglayatech.techosdeoriente.service.IPaisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaisServiceImpl implements IPaisService {

    @Autowired
    private IPaisRepository paisRepository;

    @Override
    public List<Pais> getAll() {
        return this.paisRepository.findAll();
    }

    @Override
    public Pais getPais(Integer idpais) {
        return this.paisRepository.findById(idpais).orElse(null);
    }

    @Override
    public Pais save(Pais pais) {
        return this.paisRepository.save(pais);
    }
}
