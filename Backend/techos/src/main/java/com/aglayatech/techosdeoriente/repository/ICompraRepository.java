package com.aglayatech.techosdeoriente.repository;

import com.aglayatech.techosdeoriente.model.Compra;
import com.aglayatech.techosdeoriente.model.TipoComprobante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ICompraRepository extends JpaRepository<Compra, Long> {

    @Query("Select t from TipoComprobante t")
    List<TipoComprobante> findtipos();
}
