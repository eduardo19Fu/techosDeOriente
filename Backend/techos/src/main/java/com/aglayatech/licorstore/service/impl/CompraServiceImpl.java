package com.aglayatech.licorstore.service.impl;

import com.aglayatech.licorstore.model.Compra;
import com.aglayatech.licorstore.model.TipoComprobante;
import com.aglayatech.licorstore.repository.ICompraRepository;
import com.aglayatech.licorstore.service.ICompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompraServiceImpl implements ICompraService {

    @Autowired
    private ICompraRepository compraRepository;

    @Override
    public List<Compra> getAll() {
        return this.compraRepository.findAll(Sort.by(Sort.Direction.DESC, "fechaRegistro"));
    }

    @Override
    public Compra getCompra(Integer idcompra) {
        return this.compraRepository.findById(idcompra).orElse(null);
    }

    @Override
    public Compra save(Compra compra) {
        return this.compraRepository.save(compra);
    }

    @Override
    public void delete(Integer idcompra) {
        this.compraRepository.deleteById(idcompra);
    }

    @Override
    public List<TipoComprobante> getTipos() {
        return this.compraRepository.findtipos();
    }
}
