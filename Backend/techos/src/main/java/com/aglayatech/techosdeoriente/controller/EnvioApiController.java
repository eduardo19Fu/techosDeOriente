package com.aglayatech.techosdeoriente.controller;

import com.aglayatech.techosdeoriente.generics.ErroresHandler;
import com.aglayatech.techosdeoriente.generics.Excepcion;
import com.aglayatech.techosdeoriente.model.*;
import com.aglayatech.techosdeoriente.service.IEnvioService;
import com.aglayatech.techosdeoriente.service.IEstadoService;
import com.aglayatech.techosdeoriente.service.IMovimientoProductoService;
import com.aglayatech.techosdeoriente.service.IProductoService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
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

    @Autowired
    private IProductoService productoService;

    @Autowired
    private IMovimientoProductoService movimientoProductoService;

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
        Map<String, Object> response = new HashMap<>();

        if (result.hasErrors()) {
            return new ResponseEntity<Map<String, Object>>(ErroresHandler.bingingResultErrorsHandler(result), HttpStatus.BAD_REQUEST);
        }

        try {

            envio.setEstados(envio.determinarEstadosEnvio(estadoService.findAll()));
            newEnvio = envioService.save(envio);

//            if (newEnvio != null) {
//                for (DetalleEnvio item: newEnvio.getItemsEnvio()) {
//                    updateExistencias(item.getProducto(), item.getCantidad());
//                }
//            }
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


    /**
     * Método controlador encargado de llevar a cabo el alta de un envío
     * @param envio Recibe como parámetro de la petición el envío que se desea despachar.
     * @return ResponseEntity Devuelve una respuesta en formato json con los resultados obtenidos durante la operación.
     * */

    @Secured(value = {"ROLE_ADMIN", "ROLE_INVENTARIO"})
    @PutMapping("/envios/despachar")
    public ResponseEntity<?> despachar(@RequestBody Envio envio) {

        Envio envioToDispatch = null;
        Envio envioDispatched = null;

        List<Estado> estados = null;
        Map<String, Object> response = new HashMap<>();

        try {
            envioToDispatch = envioService.getEnvio(envio.getIdEnvio());
            estados = estadoService.findAll();

            if (envioToDispatch != null) {
                envioToDispatch.setEstados(Envio.determinarEstadosEnvio(1, estados));
                envioDispatched = envioService.save(envioToDispatch);
            } else {
                response.put("mensaje", "El envio que desea despachar no se encuentra registrado en la base de datos.");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            }
        } catch (DataAccessException e) {
            return new ResponseEntity<Map<String, Object>>(Excepcion.dataAccessExceptionHandler(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "Envío despachado con éxito.");
        response.put("envio", envioDispatched);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    // APARTADO DE REPORTES
    @GetMapping("/envios/generate/{id}")
    public void getReporteEnvio(@PathVariable("id") Integer idenvio, HttpServletResponse httpServletResponse) {

        try {
            byte[] bytesEnvio = envioService.showPedido(idenvio);
            ByteArrayOutputStream output = new ByteArrayOutputStream(bytesEnvio.length);
            output.write(bytesEnvio, 0, bytesEnvio.length);

            httpServletResponse.setContentType("application/pdf");
            httpServletResponse.addHeader("Content-Disposition", "inline; filename=bill-" + idenvio + ".pdf");

            OutputStream os;

            os = httpServletResponse.getOutputStream();
            output.writeTo(os);
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JRException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/envios/realizados")
    public void getReporteEnviosRealizados(@RequestParam("fecha") String fecha, HttpServletResponse httpServletResponse) {
        try {
            byte[] bytesEnvio = envioService.rptEnviosRealizados(fecha);
            ByteArrayOutputStream output = new ByteArrayOutputStream(bytesEnvio.length);
            output.write(bytesEnvio, 0, bytesEnvio.length);

            httpServletResponse.setContentType("application/pdf");
            httpServletResponse.addHeader("Content-Disposition", "inline; filename=reporte-envios.pdf");

            OutputStream os;

            os = httpServletResponse.getOutputStream();
            output.writeTo(os);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Función encargado de la actualización de existencias
     * <p>Este se encarga de determinar la cantidad de existencias del  producto recién ingresado
     * para poder actualizar su stock de forma adecuada según los items recibidos.</p>
     * @param producto Producto a operar existencias
     * @param cantidad Cantidad de existencias a operar para la actualizacion
     *
     * */
    public void updateExistencias(Producto producto, int cantidad) {
        Producto productoUpdated = new Producto();
        producto.setStock(producto.getStock() - cantidad);
        productoUpdated = productoService.save(producto);
    }

    /**
     * Función que opera las existencias en los movimientos de producto para inventario.
     *
     * @param producto Recibe el producto como parámetro para se operado.
     * @param compra Recibe la compra generada para ser operada.
     * @param cantidad Recibe la cantidad de producto a operar
     *
     * */
    public void movimiento(Producto producto, Compra compra, int cantidad) {
        MovimientoProducto movimiento = new MovimientoProducto();

        try {
            movimiento.setTipoMovimiento(movimientoProductoService.findTipoMovimiento("envio".toUpperCase()));
            movimiento.setUsuario(compra.getUsuario());
            movimiento.setProducto(producto);
            movimiento.setStockInicial(producto.getStock());
            movimiento.setCantidad(cantidad);
            movimiento.calcularStock();

            movimientoProductoService.save(movimiento);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }
}
