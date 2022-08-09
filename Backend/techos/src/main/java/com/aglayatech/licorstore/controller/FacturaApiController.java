package com.aglayatech.licorstore.controller;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import com.aglayatech.licorstore.model.*;
import com.aglayatech.licorstore.service.*;
import com.fel.firma.emisor.FirmaEmisor;
import com.fel.firma.emisor.RespuestaServicioFirma;
import com.fel.validaciones.documento.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.sf.jasperreports.engine.JRException;

@CrossOrigin(origins = {"http://localhost:4200", "https://dtodojalapa.xyz", "http://dtodojalapa.xyz"})
@RestController
@RequestMapping(value = {"/api"})
public class FacturaApiController {

    @Autowired
    private IFacturaService serviceFactura;

    @Autowired
    private IProductoService serviceProducto;

    @Autowired
    private IEstadoService serviceEstado;

    @Autowired
    private ICorrelativoService serviceCorrelativo;

    @Autowired
    private IMovimientoProductoService serviceMovimiento;

    @Autowired
    private IUsuarioService serviceUsuario;

    // Inyeccion para capturar el Emisor del Regimen FEL
    @Autowired
    private IEmisorService serviceEmisor;

    // Inyeccion para capturar el Certificador del Regimen FEL
    @Autowired
    private ICertificadorService serviceCertificador;

    @Autowired
    private ITipoFacturaService serviceTipoFactura;

    @Secured(value = {"ROLE_ADMIN", "ROLE_COBRADOR"})
    @GetMapping(value = "/facturas")
    public List<Factura> index() {
        return this.serviceFactura.findAll();
    }

    @GetMapping(value = "/facturas/page/{page}")
    public Page<Factura> index(@PathVariable("page") Integer page) {
        return this.serviceFactura.findAll(PageRequest.of(page, 5));
    }

    @Secured(value = {"ROLE_ADMIN", "ROLE_COBRADOR"})
    @GetMapping(value = "/facturas/factura/{id}")
    public ResponseEntity<?> showFactura(@PathVariable("id") Long idfactura) {

        Factura factura = null;
        Map<String, Object> response = new HashMap<>();

        try {
            factura = serviceFactura.findFactura(idfactura);
        } catch (DataAccessException e) {
            response.put("mensaje", "¡Error en la base de datos!");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (factura == null) {
            response.put("mensaje", "¡La factura con id ".concat(idfactura.toString()).concat(" no existe en la base de datos!"));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Factura>(factura, HttpStatus.OK);
    }

    @Secured(value = {"ROLE_ADMIN", "ROLE_COBRADOR"})
    @PostMapping(value = "/facturas")
    public ResponseEntity<?> create(@RequestBody Factura factura, BindingResult result) {

        Factura newFactura = null;
        Estado estado = serviceEstado.findByEstado("PAGADO");
        Estado estadoCorr = serviceEstado.findByEstado("ACTIVO");
        Estado estadoCorrFinalizado = serviceEstado.findByEstado("FINALIZADO");
        TipoFactura tipoFactura = serviceTipoFactura.getTipoFactura(1);

        Emisor emisor = null;
        Certificador certificador = null;
        MovimientoProducto movimientoProducto = null;
        Correlativo correlativo = serviceCorrelativo.findByUsuario(factura.getUsuario(), estadoCorr);

        Map<String, Object> response = new HashMap<>();

        if (result.hasErrors()) {
            // tratamiento de errores
            List<String> errors = result.getFieldErrors().stream()
                    .map(err -> "El campo '".concat(err.getField().concat("' ")).concat(err.getDefaultMessage()))
                    .collect(Collectors.toList());

            response.put("errors", errors);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        try {

            /******************* PROCEDIMIENTO SIN REGIMEN FEL ******************/
            factura.setEstado(estado);
            factura.setTipoFactura(tipoFactura);
            if (correlativo != null) {
                newFactura = serviceFactura.save(factura);

                if (newFactura != null) {
                    correlativo.setCorrelativoActual(correlativo.getCorrelativoActual() + 1);

                    if (correlativo.getCorrelativoActual() > correlativo.getCorrelativoFinal()) {
                        correlativo.setEstado(estadoCorrFinalizado);
                        serviceCorrelativo.save();
                    }

                    serviceCorrelativo.save(correlativo);
                    // Actualiza el stock de los productos que forman parte de cada una de las lineas de la factura
                    for (DetalleFactura item : newFactura.getItemsFactura()) {
                        Producto producto = item.getProducto();
                        movimientoProducto = new MovimientoProducto();

                        movimientoProducto.setTipoMovimiento(serviceMovimiento.findTipoMovimiento("VENTA"));
                        movimientoProducto.setUsuario(factura.getUsuario());
                        movimientoProducto.setProducto(producto);
                        movimientoProducto.setStockInicial(producto.getStock());
                        movimientoProducto.setCantidad(item.getCantidad());
                        movimientoProducto.calcularStock();

                        serviceMovimiento.save(movimientoProducto);
                        serviceProducto.save(producto);
                    }
                } else {
                    response.put("mensaje", "Factura se encuentra vacía.");
                    return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
                }
            } else {
                response.put("mensaje", "No existe correlativo activo para este usuario.");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }


        } catch (DataAccessException e) {
            response.put("mensaje", "¡Error en la base de datos!");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "¡Factura creada!");
        response.put("factura", newFactura);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @Secured(value = {"ROLE_COBRADOR", "ROLE_ADMIN"})
    @DeleteMapping(value = "/facturas/cancel/{id}/{idusuario}")
    public ResponseEntity<?> cancel(@PathVariable("id") Long idfactura, @PathVariable("idusuario") Integer idusuario) {

        Map<String, Object> response = new HashMap<>();

        Factura cancelFactura = null;
        Estado estado = null;
        Usuario usuario = null;
        MovimientoProducto movimientoProducto = null;

        // DATOS RECOLECTADOS PARA FIRMAR PETICION AL CERTIFICADOR
        Emisor emisor = null;
        Certificador certificador = null;

        try {
            cancelFactura = serviceFactura.findFactura(idfactura);
            estado = serviceEstado.findByEstado("ANULADO");
            usuario = serviceUsuario.findById(idusuario);


            if (estado != null) {

                /******************* PROCEDIMIENTO SIN REGIMEN FEL ******************/

                cancelFactura.setEstado(estado);

                // Recorre el listado de items de la factura y retorna al stock la cantidad comprada
                for (DetalleFactura linea : cancelFactura.getItemsFactura()) {

                    Producto producto = linea.getProducto();
                    movimientoProducto = new MovimientoProducto();

                    movimientoProducto.setTipoMovimiento(serviceMovimiento.findTipoMovimiento("ANULACION FACTURA"));
                    movimientoProducto.setUsuario(usuario);

                    movimientoProducto.setProducto(producto);
                    movimientoProducto.setCantidad(linea.getCantidad());
                    movimientoProducto.calcularStock();

                    serviceMovimiento.save(movimientoProducto);
                    serviceProducto.save(producto);

                }

                serviceFactura.save(cancelFactura);

            } else {
                response.put("mensaje", "Estado no localizado");
                response.put("error", "El estado de anulado no pudo ser localizado");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            }

        } catch (DataAccessException e) {
            response.put("mensaje", "¡Error en la base de datos!");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "¡Factura Anulada!");
        response.put("factura", cancelFactura);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    /*************** PDF REPORTS CONTROLLERS ********************/

    // CONTROLADOR DE FACTURA
    @GetMapping(value = "/facturas/generate/{id}")
    public void generateBill(@PathVariable("id") Long idfactura, HttpServletResponse httpServletResponse)
            throws JRException, SQLException, FileNotFoundException {


        try {
            byte[] bytesFactura = serviceFactura.showBill(idfactura);
            ByteArrayOutputStream out = new ByteArrayOutputStream(bytesFactura.length);
            out.write(bytesFactura, 0, bytesFactura.length);

            httpServletResponse.setContentType("application/pdf");
            httpServletResponse.addHeader("Content-Disposition", "inline; filename=bill-" + idfactura + ".pdf");

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

    // CONTROLADOR VENTAS DIARIAS
    @GetMapping(value = "/facturas/daily-sales")
    public void dailySales(@RequestParam("usuario") String usuario, @RequestParam("fecha") String fecha, HttpServletResponse httpServletResponse)
            throws FileNotFoundException, JRException, SQLException, ParseException {

        Date fechaBusqueda;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        fechaBusqueda = format.parse(fecha);
        Integer idusuario = Integer.parseInt(usuario);

        byte[] bytesDailySalesReport = serviceFactura.resportDailySales(idusuario, fechaBusqueda);
        ByteArrayOutputStream out = new ByteArrayOutputStream(bytesDailySalesReport.length);
        out.write(bytesDailySalesReport, 0, bytesDailySalesReport.length);

        httpServletResponse.setContentType("application/pdf");
        httpServletResponse.addHeader("Content-Disposition", "inline; filename=daily-sales.pdf");

        OutputStream os;
        try {
            os = httpServletResponse.getOutputStream();
            out.writeTo(os);
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
