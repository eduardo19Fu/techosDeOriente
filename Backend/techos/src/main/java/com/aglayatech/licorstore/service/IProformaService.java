package com.aglayatech.licorstore.service;

import com.aglayatech.licorstore.model.Proforma;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IProformaService {

    public List<Proforma> findAll();

    public Page<Proforma> findAll(Pageable pageable);

    public Proforma findProforma(Long idproforma);

    public Proforma save(Proforma proforma);

}
