package com.aglayatech.techosdeoriente.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.aglayatech.techosdeoriente.model.Correlativo;
import com.aglayatech.techosdeoriente.model.Estado;
import com.aglayatech.techosdeoriente.model.Usuario;

public interface ICorrelativoService {
	
	public List<Correlativo> findAll();
	
	public Page<Correlativo> findAll(Pageable pageable);
	
	public Correlativo findById(Long idcorrelativo);
	
	public Correlativo findByUsuario(Usuario usuario, Estado estado);
	
	public Correlativo save(Correlativo correlativo);
	
	public void delete(Long id);

}
