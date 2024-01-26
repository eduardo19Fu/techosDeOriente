package com.aglayatech.techosdeoriente.service;

import com.aglayatech.techosdeoriente.model.Compra;
import com.aglayatech.techosdeoriente.model.TipoComprobante;

import java.util.List;

public interface ICompraService {

    public List<Compra> getAll();

    public Compra getCompra(Long idcompra);

    public Compra save(Compra compra);

    public void delete(Long idcompra);

    /****** Buscar Tipos de Comprobante ******/
    public List<TipoComprobante> getTipos();
}
