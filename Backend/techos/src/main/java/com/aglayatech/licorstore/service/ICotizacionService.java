package com.aglayatech.licorstore.service;

import com.aglayatech.licorstore.model.Cotizacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICotizacionService {

    public List<Cotizacion> findAll();

    public Page<Cotizacion> findAll(Pageable pageable);

    public Cotizacion findProforma(Long idproforma);

    public Cotizacion save(Cotizacion proforma);

}
