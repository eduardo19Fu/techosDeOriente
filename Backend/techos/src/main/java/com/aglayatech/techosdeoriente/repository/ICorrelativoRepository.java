package com.aglayatech.techosdeoriente.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aglayatech.techosdeoriente.model.Correlativo;
import com.aglayatech.techosdeoriente.model.Estado;
import com.aglayatech.techosdeoriente.model.Usuario;

public interface ICorrelativoRepository extends JpaRepository<Correlativo, Long> {
	
	// Buscar el correlativo del usuario logueado en el sistema
	public Optional<Correlativo> findByUsuarioAndEstado(Usuario usuario, Estado estado);

}
