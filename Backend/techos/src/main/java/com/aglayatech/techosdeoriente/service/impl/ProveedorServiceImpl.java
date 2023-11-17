package com.aglayatech.techosdeoriente.service.impl;

import com.aglayatech.techosdeoriente.model.Proveedor;
import com.aglayatech.techosdeoriente.repository.IProveedorRepository;
import com.aglayatech.techosdeoriente.service.IProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProveedorServiceImpl implements IProveedorService {

    @Autowired
    private IProveedorRepository proveedorRepository;

    @Override
    public List<Proveedor> getAll() {
        return this.proveedorRepository.findAll(Sort.by(Sort.Direction.ASC, "idProveedor"));
    }

    @Override
    public Proveedor getProveedor(Integer idproveedor) {
        return this.proveedorRepository.findById(idproveedor).orElse(null);
    }

    @Override
    public Proveedor save(Proveedor proveedor) {
        return this.proveedorRepository.save(proveedor);
    }

    @Override
    public Proveedor delete(Proveedor proveedor) {
        this.proveedorRepository.deleteById(proveedor.getIdProveedor());
        return proveedor;
    }
}
