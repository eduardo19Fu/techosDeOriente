package com.aglayatech.techosdeoriente.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aglayatech.techosdeoriente.model.MarcaProducto;

public interface IMarcaProductoRepository extends JpaRepository<MarcaProducto, Integer> {
	
	// Consulta que permite buscar marca por nombre
	@Query("Select m from MarcaProducto m where m.marca = :param1")
	List<MarcaProducto> findByMarca(@Param("param1") String marca);

}
