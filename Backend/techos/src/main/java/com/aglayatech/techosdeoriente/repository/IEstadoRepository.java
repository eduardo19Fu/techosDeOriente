package com.aglayatech.techosdeoriente.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aglayatech.techosdeoriente.model.Estado;

public interface IEstadoRepository extends JpaRepository<Estado, Integer> {
	
	Optional<Estado> findByEstado(String estado);

}
