package com.aglayatech.licorstore.controller;

import com.aglayatech.licorstore.model.Cotizacion;
import com.aglayatech.licorstore.service.ICotizacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(value = {"http://localhost:4200", "https://dtodojalapa.xyz", "http://dtodojalapa.xyz"})
@RestController
@RequestMapping("/api")
public class CotizacionApiController {

    @Autowired
    private ICotizacionService proformaService;

    @Secured(value = {"ROLE_ADMIN", "ROLE_COBRADOR"})
    @GetMapping("/proformas")
    public List<Cotizacion> index(){
        return this.proformaService.findAll();
    }

    @Secured(value = {"ROLE_ADMIN", "ROLE_COBRADOR"})
    @GetMapping("/proformas/{id}")
    public ResponseEntity<?> findProforma(@PathVariable("id") Long id){

        Map<String, Object> response = new HashMap<>();
        Cotizacion proforma = null;

        try{
            proforma = this.proformaService.findProforma(id);
        } catch(DataAccessException e){
            response.put("mensaje", "¡Ha ocurrido un error en la Base de Datos!");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(proforma == null){
            response.put("mensaje", "¡La proforma no se encuentra registrada en la Base de Datos!");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Cotizacion>(proforma, HttpStatus.OK);
    }
}
