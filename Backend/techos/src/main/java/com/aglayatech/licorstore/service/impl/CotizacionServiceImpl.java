package com.aglayatech.licorstore.service.impl;

import com.aglayatech.licorstore.model.Cotizacion;
import com.aglayatech.licorstore.repository.ICotizacionRepository;
import com.aglayatech.licorstore.service.ICotizacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CotizacionServiceImpl implements ICotizacionService {

    @Autowired
    private ICotizacionRepository proformaRepository;

    @Override
    public List<Cotizacion> findAll() {
        return this.proformaRepository.findAll(Sort.by(Direction.DESC, "fechaEmision"));
    }

    @Override
    public Page<Cotizacion> findAll(Pageable pageable) {
        return this.proformaRepository.findAll(pageable);
    }

    @Override
    public Cotizacion findProforma(Long idproforma) {
        return this.proformaRepository.findById(idproforma).orElse(null);
    }

    @Override
    public Cotizacion save(Cotizacion proforma) {
        return this.proformaRepository.save(proforma);
    }
}
