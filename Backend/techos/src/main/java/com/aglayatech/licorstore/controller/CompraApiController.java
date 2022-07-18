package com.aglayatech.licorstore.controller;

import com.aglayatech.licorstore.model.Compra;
import com.aglayatech.licorstore.model.DetalleCompra;
import com.aglayatech.licorstore.model.Estado;
import com.aglayatech.licorstore.service.ICompraService;
import com.aglayatech.licorstore.service.IEstadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(value = {"http://localhost:4200"})
@RestController
@RequestMapping("/api")
public class CompraApiController {

    @Autowired
    private ICompraService compraService;

    @Autowired
    private IEstadoService estadoService;

    @GetMapping("/compras")
    public List<Compra> index() {
        return this.compraService.getAll();
    }

    @GetMapping("/compras/{id}")
    public ResponseEntity<?> getCompra(@PathVariable("id") Integer id) {

        Map<String, Object> response = new HashMap<>();
        Compra compra = null;

        try
        {
            compra = this.compraService.getCompra(id);
        } catch(DataAccessException e)
        {
            response.put("mensaje", "¡Ha ocurrido un error en la Base de Datos!");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(compra == null)
        {
            response.put("mensaje", "¡La compra deseada no se encuentra registrada en la Base de Datos!");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }


        return new ResponseEntity<Compra>(compra, HttpStatus.OK);
    }

    @Secured(value = {"ROLE_ADMIN"})
    @PostMapping("/compras")
    public ResponseEntity<?> create(@RequestBody Compra compra, BindingResult result) {

        Map<String, Object> response = new HashMap<>();
        Compra newCompra = null;
        Estado estado = null;

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
            estado = this.estadoService.findByEstado("ACTIVO");
            compra.setEstado(estado);
            newCompra = this.compraService.save(compra);
        } catch (DataAccessException e)
        {
            response.put("mensaje", "¡Ha ocurrido un error en la Base de Datos!");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(newCompra == null)
        {
            response.put("mensaje", "¡La compra no pudo registrarse con éxito!");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        response.put("mensaje", "¡Compra registrada con éxito!");
        response.put("compra", newCompra);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @Secured(value = {"ROLE_ADMIN"})
    @PutMapping("/compras/disable")
    public ResponseEntity<?> disable(@RequestBody Compra compra) {

        Map<String, Object> response = new HashMap<>();
        Compra compraDisabled = null;
        Estado estado = null;

        List<DetalleCompra> itemsCompra = new ArrayList<>();

        try
        {
            estado = this.estadoService.findByEstado("ANULADO");
            compra.setEstado(estado);
            compraDisabled = this.compraService.save(compra);
        } catch (DataAccessException e)
        {
            response.put("mensaje", "¡Ha ocurrido un error en la Base de Datos!");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(compraDisabled == null)
        {
            response.put("mensaje", "¡Registro no pudo ser anulado!");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        response.put("mensaje", "¡Registro Anulado!");
        response.put("compra", compraDisabled);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }
}
