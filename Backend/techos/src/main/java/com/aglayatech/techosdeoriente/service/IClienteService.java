package com.aglayatech.techosdeoriente.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.aglayatech.techosdeoriente.model.Cliente;

public interface IClienteService {
	
	public List<Cliente> findAll();
	
	public Page<Cliente> findAll(Pageable pageable);
	
	public Cliente findById(Integer idcliente);
	
	public List<Cliente> findByName(String nombre);
	
	public Cliente findByNit(String nit);
	
	public Cliente save(Cliente cliente);
	
	public void delete(Integer idcliente);

	public Integer getMaxClientes();
	
}
