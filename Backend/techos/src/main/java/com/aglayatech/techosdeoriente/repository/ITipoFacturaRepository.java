package com.aglayatech.techosdeoriente.repository;

import com.aglayatech.techosdeoriente.model.TipoFactura;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITipoFacturaRepository extends JpaRepository<TipoFactura, Integer> {
}
