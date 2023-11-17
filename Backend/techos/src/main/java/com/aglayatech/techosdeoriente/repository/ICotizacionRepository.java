package com.aglayatech.techosdeoriente.repository;

import com.aglayatech.techosdeoriente.model.Cotizacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICotizacionRepository extends JpaRepository<Cotizacion, Long> {
}
