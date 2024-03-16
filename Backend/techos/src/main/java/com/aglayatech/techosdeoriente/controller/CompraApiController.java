package com.aglayatech.techosdeoriente.controller;

import com.aglayatech.techosdeoriente.generics.ErroresHandler;
import com.aglayatech.techosdeoriente.model.Compra;
import com.aglayatech.techosdeoriente.model.DetalleCompra;
import com.aglayatech.techosdeoriente.model.Estado;
import com.aglayatech.techosdeoriente.model.MovimientoProducto;
import com.aglayatech.techosdeoriente.model.Producto;
import com.aglayatech.techosdeoriente.model.TipoComprobante;
import com.aglayatech.techosdeoriente.service.ICompraService;
import com.aglayatech.techosdeoriente.service.IEstadoService;
import com.aglayatech.techosdeoriente.service.IMovimientoProductoService;
import com.aglayatech.techosdeoriente.service.IProductoService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(value = {"http://localhost:4200"})
@RestController
@RequestMapping("/api")
public class CompraApiController {

    @Autowired
    private ICompraService compraService;

    @Autowired
    private IEstadoService estadoService;

    @Autowired
    private IProductoService productoService;

    @Autowired
    private IMovimientoProductoService movimientoProductoService;

    @GetMapping("/compras")
    public List<Compra> index() {
        return this.compraService.getAll();
    }

    @GetMapping("/compras/{id}")
    public ResponseEntity<?> getCompra(@PathVariable("id") Long id) {

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
            return new ResponseEntity<Map<String, Object>>(ErroresHandler.bingingResultErrorsHandler(result), HttpStatus.BAD_REQUEST);
        }

        try
        {
            estado = this.estadoService.findByEstado("ACTIVO");
            compra.setEstado(estado);

            // Recorrer item por item para validr si los productos existen o no.
            for(DetalleCompra item : compra.getItems()) {
                if (this.crearProducto(item, compra.getFechaCompra())) {
                    this.movimiento(item.getProducto(), compra, item.getCantidad());
                }
            }

            newCompra = this.compraService.save(compra);

        } catch (DataAccessException e)
        {
            response.put("mensaje", "¡Ha ocurrido un error en la Base de Datos!");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            e.printStackTrace();
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

        try {
            estado = this.estadoService.findByEstado("ANULADO");
            compra.setEstado(estado);
            compraDisabled = this.compraService.save(compra);
        } catch (DataAccessException e) {
            response.put("mensaje", "¡Ha ocurrido un error en la Base de Datos!");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (compraDisabled == null) {
            response.put("mensaje", "¡Registro no pudo ser anulado!");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        response.put("mensaje", "¡Registro Anulado!");
        response.put("compra", compraDisabled);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @Secured(value = {"ROLE_ADMIN"})
    @DeleteMapping(value = "/compras/eliminar/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long idcompra) {

        Compra compra = null;
        Map<String, Object> response = new HashMap<>();

        try {
            compra = compraService.getCompra(idcompra);

            for(DetalleCompra item : compra.getItems()) {
                updateExistencias(item.getProducto(), item.getCantidad(), "eliminar_compra".toUpperCase());
            }

            compraService.delete(idcompra);
        } catch (DataAccessException e) {
            response.put("mensaje", "¡Ha ocurrido un error en la Base de Datos!");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "¡Compra con ID: ".concat(idcompra.toString()).concat(" fué eliminado con éxito!"));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Endpoint que devuelve los tipos de comprobante registrados en la base de datos.
     * */
    @GetMapping("/compras/tipos-comprobante/get")
    public List<TipoComprobante> tiposComprobante() {
        return this.compraService.getTipos();
    }


    /**
     * Función encargado de la actualización de existencias.
     * <p>Este se encarga de determinar la cantidad de existencias del  producto recién ingresado
     * para poder actualizar su stock de forma adecuada según los items recibidos y según el tipo de movimiento.</p>
     * <p>Si el tipo de movimiento es compra, agregará la cantidad recibida al stock del producto.</p>
     * <p>Si el tipo de movimiento es eliminar_compra, restará la cantidad agregada anteriormente al stock.</p>
     * @param producto Es el producto a procesar
     * @param cantidad Es la cantidad a agregar o quitar
     * @param tipoMovimiento Determina el tipo movimiento que se va a realizar
     * */
    public void updateExistencias(Producto producto, int cantidad, String tipoMovimiento) {
        Producto productoUpdated = new Producto();

        if(tipoMovimiento.equals("compra".toUpperCase())) {
            producto.setStock(producto.getStock() + cantidad);
        } else if(tipoMovimiento.equals("eliminar_compra".toUpperCase())) {
            producto.setStock(producto.getStock() - cantidad);
        }
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

        movimiento.setTipoMovimiento(movimientoProductoService.findTipoMovimiento("COMPRA"));
        movimiento.setUsuario(compra.getUsuario());
        movimiento.setProducto(producto);
        movimiento.setStockInicial(producto.getStock());
        movimiento.setCantidad(cantidad);
        movimiento.calcularStock();

        movimientoProductoService.save(movimiento);
    }

    /**
     * Método que recibe un producto que no existe y lo registra.
     *
     * @param item Recibe el item a analizar.
     * @param fechaIngreso Fecha de ingreso para el producto misma que la compra realizada
     * @return boolean Devuelve un valor booleano analizando si se llevo a cabo el registro con éxito o no.
     * @exception DataAccessException Lanza una posible excepcion en caso de ocurrir un problema a nivel de base de datos del tipo
     *
     * */
    private boolean crearProducto(DetalleCompra item, LocalDate fechaIngreso) {
        Producto producto = null;
        Estado estadoProductoNuevo = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            estadoProductoNuevo = this.estadoService.findByEstado("activo".toUpperCase());

              if(item.getProducto().getIdProducto() != null) {
                this.updateExistencias(item.getProducto(), item.getCantidad(), "compra".toUpperCase());
              } else {
                producto = item.getProducto();
                producto.setEstado(estadoProductoNuevo);
                producto.setFechaIngreso(simpleDateFormat.parse(fechaIngreso.toString()));
                // producto.stock() debe ir a cero para evitar que el movimiento del producto sume exitencias
                this.productoService.save(producto);
              }
            return true;
        } catch(DataAccessException | ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}
