package com.aglayatech.techosdeoriente.service.impl;

import com.aglayatech.techosdeoriente.model.Certificador;
import com.aglayatech.techosdeoriente.repository.ICertificadorRepository;
import com.aglayatech.techosdeoriente.service.ICertificadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CertificadorServiceImpl implements ICertificadorService {

    @Autowired
    private ICertificadorRepository certificadorRepository;

    @Override
    public Certificador getCertificador(Integer idcertificador) {
        return this.certificadorRepository.findById(idcertificador).orElse(null);
    }
}
