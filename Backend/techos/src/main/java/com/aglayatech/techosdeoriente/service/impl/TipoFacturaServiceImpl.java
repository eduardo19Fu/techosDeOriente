package com.aglayatech.techosdeoriente.service.impl;

import com.aglayatech.techosdeoriente.model.TipoFactura;
import com.aglayatech.techosdeoriente.repository.ITipoFacturaRepository;
import com.aglayatech.techosdeoriente.service.ITipoFacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TipoFacturaServiceImpl implements ITipoFacturaService {

    @Autowired
    private ITipoFacturaRepository tipoFacturaRepository;

    @Override
    public TipoFactura getTipoFactura(Integer id) {
        return this.tipoFacturaRepository.findById(id).orElse(null);
    }
}
