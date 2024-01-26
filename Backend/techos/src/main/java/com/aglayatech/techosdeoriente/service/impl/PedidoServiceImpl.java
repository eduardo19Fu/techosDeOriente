package com.aglayatech.techosdeoriente.service.impl;

import com.aglayatech.techosdeoriente.model.Pedido;
import com.aglayatech.techosdeoriente.repository.IPedidoRepository;
import com.aglayatech.techosdeoriente.service.IPedidoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidoServiceImpl implements IPedidoService {

    private final static Logger LOGGER = LoggerFactory.getLogger(PedidoServiceImpl.class);

    @Autowired
    private IPedidoRepository pedidoRepository;

    @Override
    public List<Pedido> getAll() {
        return pedidoRepository.findAll();
    }

    @Override
    public Pedido getPedido(Long id) {
        return pedidoRepository.findById(id).orElse(null);
    }

    @Override
    public Pedido save(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    @Override
    public void delete(Long id) {
        pedidoRepository.deleteById(id);
    }
}
