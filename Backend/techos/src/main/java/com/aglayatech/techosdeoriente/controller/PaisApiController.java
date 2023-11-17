package com.aglayatech.techosdeoriente.controller;

import com.aglayatech.techosdeoriente.model.Pais;
import com.aglayatech.techosdeoriente.service.IPaisService;
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

@CrossOrigin(value = {"https://localhost:4200"})
@RestController
@RequestMapping("/api")
public class PaisApiController {

    @Autowired
    private IPaisService paisService;

    @GetMapping("/paises")
    public List<Pais> index(){
        return this.paisService.getAll();
    }

    @Secured(value = {"ROLE_ADMIN"})
    @GetMapping("/paises/{id}")
    public ResponseEntity<?> getPais(@PathVariable("id") Integer id){

        Map<String, Object> response = new HashMap<>();
        Pais pais = null;

        try {
            pais = this.paisService.getPais(id);
        } catch(DataAccessException e) {
            response.put("mensaje", "¡Ha ocurrido un error en la Base de Datos!");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(pais == null) {
            response.put("mensaje", "¡El país no se encuentra registrado en la Base de Datos!");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Pais>(pais, HttpStatus.OK);
    }

    @Secured(value = {"ROLE_ADMIN"})
    @PostMapping("/paises")
    public ResponseEntity<?> create(@RequestBody Pais pais, BindingResult result) {

        Map<String, Object> response = new HashMap<>();
        Pais newPais = null;

        if(result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(err -> "El campo '".concat(err.getField().concat("' ")).concat(err.getDefaultMessage()))
                    .collect(Collectors.toList());

            response.put("errors", errors);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        try{
            newPais = this.paisService.save(pais);
        } catch(DataAccessException e) {
            response.put("mensaje", "¡Ha ocurrido un error en la Base de Datos!");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(newPais == null) {
            response.put("mensaje", "No se pudo llevar a cabo el registro!");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        response.put("mensaje", "Pais registrado con éxito!");
        response.put("pais", newPais);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @Secured(value = {"ROLE_ADMIN", "ROLE_INVENTARIO"})
    @PutMapping("/paises")
    public ResponseEntity<?> update(@RequestBody Pais pais, BindingResult result) {

        Map<String, Object> response = new HashMap<>();
        Pais updated = null;

        if(result.hasErrors())
        {
            List<String> errors = result.getFieldErrors().stream()
                    .map(err -> "El campo '".concat(err.getField().concat("' ")).concat(err.getDefaultMessage()))
                    .collect(Collectors.toList());

            response.put("errors", errors);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);

        }

        try
        {
            updated = this.paisService.save(pais);
        } catch(DataAccessException e)
        {
            response.put("mensaje", "¡Ha ocurrido un error en la Base de Datos!");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "Pais actualizado con éxito!");
        response.put("pais", updated);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }
}
