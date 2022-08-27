package com.aglayatech.licorstore.controller;

import com.aglayatech.licorstore.generics.ErroresHandler;
import com.aglayatech.licorstore.generics.Excepcion;
import com.aglayatech.licorstore.model.Envio;
import com.aglayatech.licorstore.model.Estado;
import com.aglayatech.licorstore.service.IEnvioService;
import com.aglayatech.licorstore.service.IEstadoService;
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

@CrossOrigin(value = {"http://localhost:4200"})
@RestController
@RequestMapping("/api")
public class EnvioApiController {

    @Autowired
    private IEnvioService envioService;

    @Autowired
    private IEstadoService estadoService;

    @GetMapping("/envios")
    public List<Envio> index() {
        return envioService.getAll();
    }

    @Secured(value = {"ROLE_ADMIN", "ROLE_INVENTARIO"})
    @GetMapping("/envios/{id}")
    public ResponseEntity<?> findEnvio(@PathVariable("id") Integer id) {
        Envio envio = null;
        Map<String, Object> response = new HashMap<>();

        try {
            envio = envioService.getEnvio(id);
        } catch (DataAccessException e) {
            return new ResponseEntity<Map<String, Object>>(Excepcion.dataAccessExceptionHandler(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (envio == null) {
            response.put("mensaje", "¡El envío no se encuentra registrado en la Base de Datos!");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Envio>(envio, HttpStatus.OK);
    }

    @Secured(value = {"ROLE_ADMIN", "ROLE_INVENTARIO"})
    @PostMapping("/envios")
    public ResponseEntity<?> create(@RequestBody Envio envio, BindingResult result) {

        Envio newEnvio = null;
        Estado estadoInicial = null;
        Map<String, Object> response = new HashMap<>();

        if (result.hasErrors()) {
            return new ResponseEntity<Map<String, Object>>(ErroresHandler.bingingResultErrorsHandler(result), HttpStatus.BAD_REQUEST);
        }

        try {
            estadoInicial = estadoService.findByEstado("Pendiente".toUpperCase());
            envio.setEstados(estadoInicial);
            newEnvio = envioService.save(envio);
        } catch (DataAccessException e) {
            return new ResponseEntity<Map<String, Object>>(Excepcion.dataAccessExceptionHandler(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (newEnvio == null) {
            response.put("mensaje", "No se ha podido generar el envío deseado");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        response.put("mensaje", "El envío has sido generado con éxito.");
        response.put("envio", newEnvio);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @Secured(value = {"ROLE_ADMIN", "ROLE_INVENTARIO"})
    @PutMapping("/envios/cancel/{id}")
    public ResponseEntity<?> cancel(@PathVariable("id") Integer id) {
        return null;
    }
}
