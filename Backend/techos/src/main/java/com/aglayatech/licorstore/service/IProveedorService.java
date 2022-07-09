package com.aglayatech.licorstore.service;

import com.aglayatech.licorstore.model.Proveedor;

import java.util.List;

public interface IProveedorService {

    public List<Proveedor> getAll();

    public Proveedor getProveedor(Integer idproveedor);

    public Proveedor save(Proveedor proveedor);

    public Proveedor delete(Proveedor proveedor);
}
