package com.aglayatech.techosdeoriente.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aglayatech.techosdeoriente.model.Factura;
import org.springframework.data.jpa.repository.Query;

public interface IFacturaRepository extends JpaRepository<Factura, Long> {

    @Query(value = "Select get_cant_ventas()", nativeQuery = true)
    Integer getMaxVentas();
}
