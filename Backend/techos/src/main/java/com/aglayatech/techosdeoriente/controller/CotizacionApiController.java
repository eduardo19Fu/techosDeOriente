package com.aglayatech.techosdeoriente.controller;

import com.aglayatech.techosdeoriente.generics.ErroresHandler;
import com.aglayatech.techosdeoriente.model.Cotizacion;
import com.aglayatech.techosdeoriente.model.Estado;
import com.aglayatech.techosdeoriente.service.ICotizacionService;
import com.aglayatech.techosdeoriente.service.IEstadoService;
import net.sf.jasperreports.engine.JRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(value = {"*"})
@RestController
@RequestMapping("/api")
public class CotizacionApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CotizacionApiController.class);

    @Autowired
    private ICotizacionService proformaService;

    @Autowired
    private IEstadoService estadoService;

    @Secured(value = {"ROLE_ADMIN", "ROLE_COBRADOR"})
    @GetMapping("/cotizaciones")
    public List<Cotizacion> index(){
        return this.proformaService.findAll();
    }

    @Secured(value = {"ROLE_ADMIN", "ROLE_COBRADOR"})
    @GetMapping("/cotizaciones/{id}")
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

    @Secured(value = {"ROLE_ADMIN", "ROLE_COBRADOR"})
    @PostMapping(value = "/cotizaciones/create")
    public ResponseEntity<?> create(@RequestBody Cotizacion cotizacion, BindingResult result) {
        Map<String, Object> response = new HashMap<>();
        Cotizacion newProforma = null;
        Estado estado = null;

        if(result.hasErrors())
        {
            return new ResponseEntity<Map<String, Object>>(ErroresHandler.bingingResultErrorsHandler(result), HttpStatus.BAD_REQUEST);
        }

        try
        {
            estado = this.estadoService.findByEstado("ACTIVO");
            cotizacion.setEstado(estado);

            newProforma = this.proformaService.save(cotizacion);

        } catch (DataAccessException e) {
            response.put("mensaje", "¡Ha ocurrido un error en la Base de Datos!");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(newProforma == null)
        {
            response.put("mensaje", "¡La proforma no pudo registrarse con éxito!");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        response.put("mensaje", "¡Cotización registrada con éxito!");
        response.put("proforma", newProforma);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @Secured(value = {"ROLE_ADMIN"})
    @DeleteMapping(value = "/cotizaciones/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            proformaService.delete(id);
        } catch(DataAccessException e) {
            response.put("mensaje", "¡Ha ocurrido un error en la Base de Datos!");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "¡La proforma ha sido eliminado con éxito!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/cotizaciones/generate/{id}")
    public void generateBill(@PathVariable("id") Long idcotizacion, HttpServletResponse httpServletResponse)
            throws JRException, SQLException, FileNotFoundException {


        try {
            byte[] bytesCotizacion = proformaService.showCotizacion(idcotizacion);
            ByteArrayOutputStream out = new ByteArrayOutputStream(bytesCotizacion.length);
            out.write(bytesCotizacion, 0, bytesCotizacion.length);

            httpServletResponse.setContentType("application/pdf");
            httpServletResponse.addHeader("Content-Disposition", "inline; filename=proforma-" + idcotizacion + ".pdf");

            OutputStream os;

            os = httpServletResponse.getOutputStream();
            out.writeTo(os);
            os.flush();
            os.close();
        } catch (IOException e) {
            // new ServletException(e);
            e.printStackTrace();
        }
    }
}
