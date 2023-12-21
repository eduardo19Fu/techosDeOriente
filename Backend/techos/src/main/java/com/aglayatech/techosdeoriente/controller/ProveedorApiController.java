package com.aglayatech.techosdeoriente.controller;

import com.aglayatech.techosdeoriente.model.Estado;
import com.aglayatech.techosdeoriente.model.Proveedor;
import com.aglayatech.techosdeoriente.service.IEstadoService;
import com.aglayatech.techosdeoriente.service.IProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(value = {"http://localhost:4200"})
@RestController
@RequestMapping("/api")
public class ProveedorApiController {

    @Autowired
    private IProveedorService proveedorService;

    @Autowired
    private IEstadoService estadoService;

    @GetMapping("/proveedores")
    public List<Proveedor> index(){
        return this.proveedorService.getAll();
    }

    @Secured(value = {"ROLE_ADMIN"})
    @GetMapping("/proveedores/{id}")
    public ResponseEntity<?> getProveedor(@PathVariable("id") Integer id){

        Map<String, Object> response = new HashMap<>();
        Proveedor proveedor = null;

        try{
            proveedor = this.proveedorService.getProveedor(id);
        } catch(DataAccessException e) {
            response.put("mensaje", "¡Ha ocurrido un error en la Base de Datos!");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(proveedor == null){
            response.put("mensaje", "El proveedor no se encuentra registrado en la Base de Datos");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Proveedor>(proveedor, HttpStatus.OK);
    }

    @Secured(value = {"ROLE_ADMIN"})
    @PostMapping("/proveedores")
    public ResponseEntity<?> create(@RequestBody Proveedor proveedor, BindingResult result){

        Proveedor newProveedor = null;
        Map<String, Object> response = new HashMap<>();

        if(result.hasErrors()){
            List<String> errors = result.getFieldErrors().stream()
                    .map(err -> "El campo '".concat(err.getField().concat("' ")).concat(err.getDefaultMessage()))
                    .collect(Collectors.toList());

            response.put("errors", errors);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            Estado estado = estadoService.findByEstado("ACTIVO");
            proveedor.setEstado(estado);
            newProveedor = this.proveedorService.save(proveedor);
        } catch(DataAccessException e) {
            response.put("mensaje", "¡Ha ocurrido un error en la Base de Datos!");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(newProveedor == null){
            response.put("mensaje", "¡El proveedor no pudo ser registrado!");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        response.put("mensaje", "¡Proveedor ha sido registrado con éxito!");
        response.put("proveedor", newProveedor);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);

    }
    @Secured(value = {"ROLE_ADMIN", "ROLE_INVENTARIO"})
    @PutMapping("/proveedores")
    public ResponseEntity<?> update(@RequestBody Proveedor proveedor, BindingResult result) {

        Map<String, Object> response = new HashMap<>();
        Proveedor updated = null;

        if(result.hasErrors())
        {
            List<String> errors = result.getFieldErrors().stream()
                    .map(err -> "El campo '".concat(err.getField().concat("' ")).concat(err.getDefaultMessage()))
                    .collect(Collectors.toList());

            response.put("errors", errors);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            updated = this.proveedorService.save(proveedor);
        } catch(DataAccessException e) {
            response.put("mensaje", "¡Ha ocurrido un error en la Base de Datos!");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "¡Proveedor ha sido actualizado con éxito!");
        response.put("proveedor", updated);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }
}
