package com.aglayatech.techosdeoriente.service;

import com.aglayatech.techosdeoriente.model.Proveedor;

import java.util.List;

public interface IProveedorService {

    public List<Proveedor> getAll();

    public Proveedor getProveedor(Integer idproveedor);

    public Proveedor save(Proveedor proveedor);

    public Proveedor delete(Proveedor proveedor);
}
