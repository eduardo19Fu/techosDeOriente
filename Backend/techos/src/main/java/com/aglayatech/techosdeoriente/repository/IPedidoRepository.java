package com.aglayatech.techosdeoriente.repository;

import com.aglayatech.techosdeoriente.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPedidoRepository extends JpaRepository<Pedido, Long> {
}
