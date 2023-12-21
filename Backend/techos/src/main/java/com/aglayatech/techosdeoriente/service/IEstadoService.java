package com.aglayatech.techosdeoriente.service;

import java.util.List;

import com.aglayatech.techosdeoriente.model.Estado;

public interface IEstadoService {
	
	public List<Estado> findAll();
	
	public Estado findById(Integer idestado);
	
	public Estado findByEstado(String estado);
	
	public Estado save(Estado estado);
	
	public void delete(Integer idestado);

}
