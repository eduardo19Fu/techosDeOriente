package com.aglayatech.licorstore.controller;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

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

import com.aglayatech.licorstore.model.Correlativo;
import com.aglayatech.licorstore.model.DetalleFactura;
import com.aglayatech.licorstore.model.Estado;
import com.aglayatech.licorstore.model.Factura;
import com.aglayatech.licorstore.model.MovimientoProducto;
import com.aglayatech.licorstore.model.Producto;
import com.aglayatech.licorstore.model.Usuario;
import com.aglayatech.licorstore.service.ICorrelativoService;
import com.aglayatech.licorstore.service.IEstadoService;
import com.aglayatech.licorstore.service.IFacturaService;
import com.aglayatech.licorstore.service.IMovimientoProductoService;
import com.aglayatech.licorstore.service.IProductoService;
import com.aglayatech.licorstore.service.IUsuarioService;

import net.sf.jasperreports.engine.JRException;

@CrossOrigin(origins = {"http://localhost:4200", "https://dimsa-c60bf.web.app"})
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
	
	@GetMapping(value = "/facturas")
	public List<Factura> index(){
		return this.serviceFactura.findAll();
	}
	
	@GetMapping(value = "/facturas/page/{page}")
	public Page<Factura> index(@PathVariable("page") Integer page){
		return this.serviceFactura.findAll(PageRequest.of(page, 5));
	}
	
	@GetMapping(value = "/facturas/factura/{id}")
	public ResponseEntity<?> showFactura(@PathVariable("id") Long idfactura){
		
		Factura factura = null;
		Map<String, Object> response = new HashMap<>();
		
		try {
			factura = serviceFactura.findFactura(idfactura);
		} catch (DataAccessException e) {
			response.put("mensaje", "¡Error en la base de datos!");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(factura == null) {
			response.put("mensaje", "¡La factura con id ".concat(idfactura.toString()).concat(" no existe en la base de datos!"));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<Factura>(factura, HttpStatus.OK);
	}
	
	@PostMapping(value = "/facturas")
	public ResponseEntity<?> create(@RequestBody Factura factura, BindingResult result){
		
		Factura newFactura = null;
		Estado estado = serviceEstado.findByEstado("PAGADO");
		Estado estadoCorr = serviceEstado.findByEstado("ACTIVO");
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
			factura.setEstado(estado);
			newFactura = serviceFactura.save(factura);
			
			if(newFactura != null) {
				correlativo.setCorrelativoActual(correlativo.getCorrelativoActual() + 1);
				serviceCorrelativo.save(correlativo);
				
				// Actualiza el stock de los productos que forman parte de cada una de las lineas de la factura
				for(DetalleFactura item : newFactura.getItemsFactura()) {
					Producto producto = item.getProducto();
					movimientoProducto = new MovimientoProducto();
					
					movimientoProducto.setTipoMovimiento("VENTA");
					movimientoProducto.setUsuario(factura.getUsuario());
					movimientoProducto.setProducto(producto);
					movimientoProducto.setStockInicial(producto.getStock());
					movimientoProducto.setCantidad(item.getCantidad());
					movimientoProducto.calcularStock();
					
					serviceMovimiento.save(movimientoProducto);
					serviceProducto.save(producto);
				}
			}
			
		} catch (DataAccessException e) {
			response.put("mensaje", "¡Error en la base de datos!");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "¡La factura ha sido creada con éxito!");
		response.put("factura", newFactura);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@Secured(value = {"ROLE_COBRADOR", "ROLE_ADMIN"})
	@DeleteMapping(value = "/facturas/cancel/{id}/{idusuario}")
	public ResponseEntity<?> cancel(@PathVariable("id") Long idfactura, @PathVariable("idusuario") Integer idusuario){
		
		Factura cancelFactura = null;
		Estado estado = null;
		Usuario usuario = null;
		MovimientoProducto movimientoProducto = null;
		Map<String, Object> response = new HashMap<>();
		
		try {
			cancelFactura = serviceFactura.findFactura(idfactura);
			estado = serviceEstado.findByEstado("ANULADO");
			usuario = serviceUsuario.findById(idusuario);
			
			
			if(estado != null) {
				cancelFactura.setEstado(estado);
				
				// Recorre el listado de items de la factura y retorna al stock la cantidad comprada
				for(DetalleFactura linea : cancelFactura.getItemsFactura()) {
					
					Producto producto = linea.getProducto();
					movimientoProducto = new MovimientoProducto();
					
					movimientoProducto.setTipoMovimiento("ANULACION FACTURA");
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
			httpServletResponse.addHeader("Content-Disposition", "inline; filename=bill-"+idfactura+".pdf");
			
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
