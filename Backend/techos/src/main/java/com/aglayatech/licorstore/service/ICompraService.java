package com.aglayatech.licorstore.service;

import com.aglayatech.licorstore.model.Compra;

import java.util.List;

public interface ICompraService {

    public List<Compra> getAll();

    public Compra getCompra(Integer idcompra);

    public Compra save(Compra compra);

    public void delete(Integer idcompra);
}
