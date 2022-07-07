package com.aglayatech.licorstore.service.impl;

import com.aglayatech.licorstore.model.Proforma;
import com.aglayatech.licorstore.repository.IProformaRepository;
import com.aglayatech.licorstore.service.IProformaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProformaServiceImpl implements IProformaService {

    @Autowired
    private IProformaRepository proformaRepository;

    @Override
    public List<Proforma> findAll() {
        return this.proformaRepository.findAll(Sort.by(Direction.DESC, "fechaEmision"));
    }

    @Override
    public Page<Proforma> findAll(Pageable pageable) {
        return this.proformaRepository.findAll(pageable);
    }

    @Override
    public Proforma findProforma(Long idproforma) {
        return this.proformaRepository.findById(idproforma).orElse(null);
    }

    @Override
    public Proforma save(Proforma proforma) {
        return this.proformaRepository.save(proforma);
    }
}
