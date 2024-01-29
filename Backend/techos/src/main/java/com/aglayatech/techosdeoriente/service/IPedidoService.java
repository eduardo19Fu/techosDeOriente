package com.aglayatech.techosdeoriente.service;

import com.aglayatech.techosdeoriente.model.Pedido;

import java.util.List;

public interface IPedidoService {

    public List<Pedido> getAll();

    public Pedido getPedido(Long id);

    public Pedido save(Pedido pedido);

    public void delete(Long id);


}
