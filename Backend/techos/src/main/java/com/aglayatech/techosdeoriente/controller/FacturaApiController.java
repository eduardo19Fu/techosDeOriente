package com.aglayatech.techosdeoriente.controller;

import java.io.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletResponse;

import com.aglayatech.techosdeoriente.generics.ErroresHandler;
import com.aglayatech.techosdeoriente.generics.Excepcion;
import com.aglayatech.techosdeoriente.model.*;
import com.aglayatech.techosdeoriente.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(FacturaApiController.class);

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

    @Autowired
    private IEnvioService serviceEnvio;

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
        Envio envio = null;

        Estado estado = serviceEstado.findByEstado("PAGADO");
        Estado estadoCorr = serviceEstado.findByEstado("ACTIVO");
        Estado estadoCorrFinalizado = serviceEstado.findByEstado("FINALIZADO");
        TipoFactura tipoFactura = serviceTipoFactura.getTipoFactura(1);
        Correlativo correlativo = serviceCorrelativo.findByUsuario(factura.getUsuario(), estadoCorr);

        List<Estado> estados = new ArrayList<>();
        Map<String, Object> response = new HashMap<>();

        if (result.hasErrors()) {
            return new ResponseEntity<Map<String, Object>>(ErroresHandler.bingingResultErrorsHandler(result), HttpStatus.BAD_REQUEST);
        }

        try {

            /******************* PROCEDIMIENTO SIN REGIMEN FEL ******************/
            factura.setEstado(estado);
            estados = serviceEstado.findAll();

            if (correlativo != null) {
                newFactura = serviceFactura.save(factura);

                if (newFactura != null) {
                    correlativo.setCorrelativoActual(correlativo.getCorrelativoActual() + 1);

                    if (correlativo.getCorrelativoActual() > correlativo.getCorrelativoFinal()) {
                        correlativo.setEstado(estadoCorrFinalizado);
                        correlativo.setCorrelativoActual(correlativo.getCorrelativoFinal());
                    }

                    serviceCorrelativo.save(correlativo);

                    // Actualiza el stock de los productos que forman parte de cada una de las lineas de la factura
                    for (DetalleFactura item : newFactura.getItemsFactura()) {
                        if (this.updateExistencias(item.getProducto(), item.getCantidad(), "venta".toUpperCase())){
                            this.movimiento(item.getProducto(), newFactura, item.getCantidad(), "venta".toUpperCase());
                        }
                    }
                } else {
                    response.put("mensaje", "Factura no pudo ser registrada..");
                    return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
                }
            } else {
                response.put("mensaje", "No existe correlativo activo para este usuario.");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }

            // ACTUALIZAR LOS ESTADOS Y LA FACTURA DEL ENVIO
            if (factura.getTipoFactura().getIdTipoFactura() == 3) {
                envio = factura.getEnvio();
                envio.setEstados(Envio.determinarEstadosEnvio(2, estados));
                envio.setFactura(newFactura);

                serviceEnvio.save(envio);
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
                    if (this.updateExistencias(linea.getProducto(), linea.getCantidad(), "anulacion factura".toUpperCase())) {
                        this.movimiento(linea.getProducto(), cancelFactura, linea.getCantidad(), "anulacion factura".toUpperCase());
                    }
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

    @GetMapping("/facturas/max-ventas/get")
    public ResponseEntity<?> getMaxVentasController() {
        Integer maxVentas = 0;
        Map<String, Object> response = new HashMap<>();

        try {
            maxVentas = serviceFactura.getMaxVentas();
        } catch (DataAccessException e) {
            return new ResponseEntity<Map<String, Object>>(Excepcion.dataAccessExceptionHandler(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<Integer>(maxVentas, HttpStatus.OK);
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

        Date fechaBusqueda = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        fechaBusqueda = format.parse(fecha);
        Integer idusuario = Integer.parseInt(usuario);

        log.info("Fecha de Buscqueda para la poliza de usuario: " + fechaBusqueda.toString());
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

    @GetMapping(value = "/facturas/monthly-sales")
    public void dailySales(@RequestParam("year") String year, HttpServletResponse httpServletResponse) throws JRException, SQLException, FileNotFoundException {
        Integer anho = Integer.parseInt(year);

        byte[] bytesMonthlySalesReport = serviceFactura.reportMonthlySales(anho);
        ByteArrayOutputStream out = new ByteArrayOutputStream(bytesMonthlySalesReport.length);
        out.write(bytesMonthlySalesReport, 0, bytesMonthlySalesReport.length);

        httpServletResponse.setContentType("application/pdf");
        httpServletResponse.addHeader("Content-Disposition", "inline; filename=monthly-sales.pdf");

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

    @GetMapping(value = "/facturas/all-dailly-sales")
    public void allDailySales(@RequestParam("fecha") String fecha, HttpServletResponse httpServletResponse)
            throws JRException, SQLException, FileNotFoundException, ParseException
    {

        byte[] bytesAllDailySales = serviceFactura.reportAllDailySales(fecha);
        ByteArrayOutputStream out = new ByteArrayOutputStream(bytesAllDailySales.length);
        out.write(bytesAllDailySales, 0, bytesAllDailySales.length);

        httpServletResponse.setContentType("application/pdf");
        httpServletResponse.addHeader("Content-Disposition", "inline; filename=all-daily-sales.pdf");

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

    /**
     * metodo que actualiza las existencias según el tipo de movimiento que se este registrando
     * @param producto
     * @param cantidad
     * @param tipoMovimiento
     * */
    private boolean updateExistencias(Producto producto, int cantidad, String tipoMovimiento) {
        Producto productoUpdated = new Producto();

        if (tipoMovimiento.equals("venta".toUpperCase())) {
            producto.setStock(producto.getStock() - cantidad);
        } else if (tipoMovimiento.equals("anulacion factura".toUpperCase())) {
            producto.setStock(producto.getStock() + cantidad);
        }

        productoUpdated = serviceProducto.save(producto);
        return (productoUpdated != null ? true : false);
    }

    private void movimiento(Producto producto, Factura factura, int cantidad, String tipoMovimiento) {
        MovimientoProducto movimiento = new MovimientoProducto();

        movimiento.setTipoMovimiento(serviceMovimiento.findTipoMovimiento(tipoMovimiento));
        movimiento.setUsuario(factura.getUsuario());
        movimiento.setProducto(producto);
        movimiento.setStockInicial(producto.getStock());
        movimiento.setCantidad(cantidad);

        serviceMovimiento.save(movimiento);
    }
}
