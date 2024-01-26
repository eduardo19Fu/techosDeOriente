package com.aglayatech.techosdeoriente.controller;

import com.aglayatech.techosdeoriente.generics.ErroresHandler;
import com.aglayatech.techosdeoriente.model.DetalleCompra;
import com.aglayatech.techosdeoriente.model.Estado;
import com.aglayatech.techosdeoriente.model.Pedido;
import com.aglayatech.techosdeoriente.service.IEstadoService;
import com.aglayatech.techosdeoriente.service.IPedidoService;
import com.aglayatech.techosdeoriente.service.IProductoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(value = {"http://localhost:4200"})
@RestController
@RequestMapping(value = "/api")
public class PedidoApiController {

    private final static Logger LOGGER = LoggerFactory.getLogger(PedidoApiController.class);

    @Autowired
    private IPedidoService pedidoService;

    @Autowired
    private IEstadoService estadoService;

    @Autowired
    private IProductoService productoService;

    @GetMapping(value = "/pedidos")
    public List<Pedido> index() {
        return pedidoService.getAll();
    }

    @GetMapping(value = "/pedidos/{id}")
    public ResponseEntity<?> getPedido(@PathVariable("idpedido") Long id) {
        Map<String, Object> response = new HashMap<>();
        Pedido pedido = null;

        try
        {
            pedido = this.pedidoService.getPedido(id);
        } catch(DataAccessException e)
        {
            response.put("mensaje", "¡Ha ocurrido un error en la Base de Datos!");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(pedido == null)
        {
            response.put("mensaje", "¡El pedido deseado no se encuentra registrada en la Base de Datos!");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }


        return new ResponseEntity<Pedido>(pedido, HttpStatus.OK);
    }

    @Secured(value = {"ROLE_ADMIN", "ROLE_INVENTARIO"})
    @PostMapping(value = "/pedidos")
    public ResponseEntity<?> create(@RequestBody Pedido pedido, BindingResult result) {
        Map<String, Object> response = new HashMap<>();
        Pedido newPedido = null;
        Estado estado = null;

        if(result.hasErrors())
        {
            return new ResponseEntity<Map<String, Object>>(ErroresHandler.bingingResultErrorsHandler(result), HttpStatus.BAD_REQUEST);
        }

        try
        {
            estado = this.estadoService.findByEstado("ACTIVO");
            pedido.setEstado(estado);

//            // Recorrer item por item para validr si los productos existen o no.
//            for(DetalleCompra item : pedido.getItems()) {
//                if (this.crearProducto(item, compra.getFechaCompra())) {
//                    this.movimiento(item.getProducto(), compra, item.getCantidad());
//                }
//            }

            newPedido = this.pedidoService.save(pedido);

        } catch (DataAccessException e) {
            response.put("mensaje", "¡Ha ocurrido un error en la Base de Datos!");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            e.printStackTrace();
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(newPedido == null)
        {
            response.put("mensaje", "¡La compra no pudo registrarse con éxito!");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        response.put("mensaje", "¡Pedido registrado con éxito!");
        response.put("pedido", newPedido);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @Secured(value = {"ROLE_ADMIN", "ROLE_INVENTARIO"})
    @PutMapping(value = "/pedidos/disable")
    public ResponseEntity<?> disable (@RequestBody Pedido pedido) {
        Map<String, Object> response = new HashMap<>();
        Pedido pedidoDisabled = null;
        Estado estado = null;

        List<DetalleCompra> itemsCompra = new ArrayList<>();

        try {
            estado = this.estadoService.findByEstado("ANULADO");
            pedido.setEstado(estado);
            pedidoDisabled = this.pedidoService.save(pedido);
        } catch (DataAccessException e) {
            response.put("mensaje", "¡Ha ocurrido un error en la Base de Datos!");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (pedidoDisabled == null) {
            response.put("mensaje", "¡Registro no pudo ser anulado!");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        response.put("mensaje", "¡Registro Anulado!");
        response.put("pedido", pedidoDisabled);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

}
