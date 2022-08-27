package com.aglayatech.licorstore.service.impl;

import com.aglayatech.licorstore.model.Envio;
import com.aglayatech.licorstore.repository.IEnvioRepository;
import com.aglayatech.licorstore.service.IEnvioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnvioServiceImpl implements IEnvioService {

    @Autowired
    private IEnvioRepository envioRepository;

    @Override
    public List<Envio> getAll() {
        return envioRepository.findAll();
    }

    @Override
    public Envio getEnvio(Integer id) {
        return envioRepository.findById(id).orElse(null);
    }

    @Override
    public Envio save(Envio envio) {
        return envioRepository.save(envio);
    }

    @Override
    public void delete(Integer id) {
        envioRepository.deleteById(id);
    }
}
