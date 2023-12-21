package com.aglayatech.techosdeoriente.service.impl;

import com.aglayatech.techosdeoriente.model.Emisor;
import com.aglayatech.techosdeoriente.repository.IEmisorRepository;
import com.aglayatech.techosdeoriente.service.IEmisorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmisorServiceImpl implements IEmisorService {

    @Autowired
    private IEmisorRepository emisorRepository;

    @Override
    public Emisor getEmisor(Integer idemisor) {
        return this.emisorRepository.findById(idemisor).orElse(null);
    }
}
