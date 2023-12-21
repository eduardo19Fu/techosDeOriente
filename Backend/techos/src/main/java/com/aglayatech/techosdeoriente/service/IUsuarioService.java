package com.aglayatech.techosdeoriente.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.aglayatech.techosdeoriente.model.Role;
import com.aglayatech.techosdeoriente.model.Usuario;

public interface IUsuarioService {
	
	public List<Usuario> findAll();
	
	public Page<Usuario> findAll(Pageable pageable);
	
	public Usuario findById(Integer idusaurio);
	
	public Usuario save(Usuario usuario);
	
	public Usuario findByUsuario(String usuario);
	
	public void delete(Integer id);
	
	public List<Usuario> cajeros();

	public Integer getMaxUsuarios();
	
	// método encargado de recolectar los roles de la BD
	public List<Role> findRoles();

}
