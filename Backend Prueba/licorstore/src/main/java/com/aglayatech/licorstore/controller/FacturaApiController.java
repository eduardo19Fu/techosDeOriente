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

@CrossOrigin(origins = {"http://localhost:4200", "https://31.220.56.29"})
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
	public List<Factura> index(){
		return this.serviceFactura.findAll();
	}
	
	@GetMapping(value = "/facturas/page/{page}")
	public Page<Factura> index(@PathVariable("page") Integer page){
		return this.serviceFactura.findAll(PageRequest.of(page, 5));
	}

	@Secured(value = {"ROLE_ADMIN", "ROLE_COBRADOR"})
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

	@Secured(value = {"ROLE_ADMIN", "ROLE_COBRADOR"})
	@PostMapping(value = "/facturas")
	public ResponseEntity<?> create(@RequestBody Factura factura, BindingResult result){
		
		Factura newFactura = null;
		Estado estado = serviceEstado.findByEstado("PAGADO");
		Estado estadoCorr = serviceEstado.findByEstado("ACTIVO");
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
			if(factura != null){

				/*************** REGIMEN FEL *******************/

				emisor = this.serviceEmisor.getEmisor(1);
				certificador = this.serviceCertificador.getCertificador(1);

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'-06:00'", Locale.getDefault());
				sdf.setTimeZone(TimeZone.getTimeZone("America/Guatemala"));

				DocumentoFel documento_fel = new DocumentoFel();

				DatosEmisor datos_emisor = new DatosEmisor();
				datos_emisor.setAfiliacionIVA("GEN");
				datos_emisor.setCodigoEstablecimiento(1);
				datos_emisor.setCodigoPostal(emisor.getCodigoPostal());
				datos_emisor.setCorreoEmisor(emisor.getCorreoEmisor());
				datos_emisor.setDepartamento(emisor.getDepartamento());
				datos_emisor.setMunicipio(emisor.getMunicipio());
				datos_emisor.setDireccion(emisor.getDireccion());
				datos_emisor.setNITEmisor(emisor.getNit());
				datos_emisor.setNombreComercial(emisor.getNombreComercial());
				datos_emisor.setNombreEmisor(emisor.getNombreEmisor());
				datos_emisor.setPais("GT");
				documento_fel.setDatos_emisor(datos_emisor);

				DatosGenerales datos_generales = new DatosGenerales();
				datos_generales.setCodigoMoneda("GTQ");
				datos_generales.setFechaHoraEmision(sdf.format(new Date()));
				//datos_generales.setNumeroAcceso(888777666); //OPCIONAL
				datos_generales.setTipo("FACT");
				// datos_generales.setExportacion("SI"); //OPCIONAL
				documento_fel.setDatos_generales(datos_generales);

				// DATOS DEL CLIENTE QUE RECIBIRÁ LA FACTURA POR LA COMPRA REALIZADA
				// OBTENERLOS DE LA BUSQUEDA REALIZADA SIN '-' EN EL NIT
				DatosReceptor datos_receptor = new DatosReceptor();
				datos_receptor.setCodigoPostal("01001");
				datos_receptor.setCorreoReceptor(""); // Quien recibe el pdf por correo, pueden ir varios separados por ;
				datos_receptor.setDepartamento("JALAPA");
				datos_receptor.setDireccion(factura.getCliente().getDireccion().trim());

				if(factura.getCliente().getNit().equals("C/F"))
					datos_receptor.setIDReceptor(factura.getCliente().getNit().replace("/", "").trim());
				else
					datos_receptor.setIDReceptor(factura.getCliente().getNit().replace("-", "").trim());
					datos_receptor.setMunicipio("JALAPA");
					datos_receptor.setNombreReceptor(factura.getCliente().getNombre().trim());
					datos_receptor.setPais("GT");
					documento_fel.setDatos_receptor(datos_receptor);

				// NO MOVER ESTO
				for (int i = 1; i <= 1; i++) {
					Frases frases = new Frases();
					frases.setCodigoEscenario(i);
					frases.setTipoFrase(1);
					documento_fel.setFrases(frases);
				}

				// DATOS DEL DETALLE DE LA FACTURA
				for (int i = 0; i < factura.getItemsFactura().size(); i++) {
					Producto producto = factura.getItemsFactura().get(i).getProducto();
					// producto = producto.buscarProducto(modelo.getValueAt(i, 1).toString());
					Items items = new Items();
					items.setNumeroLinea(i + 1);
					items.setBienOServicio("B");
					items.setCantidad((double) factura.getItemsFactura().get(i).getCantidad());
					items.setDescripcion(factura.getItemsFactura().get(i).getProducto().getNombre());

					// Descuento siempre debe ir a cero para reflejarlo despues en el precio unitario
					items.setDescuento(0.0);
					// items.setDescuento((double) ((producto.getPrecio_venta() * items.getCantidad()) * (Double.parseDouble(modelo.getValueAt(i, 5).toString()))));
					// items.setDescuento(((producto.getPrecioVenta() * items.getCantidad()) * (factura.getItemsFactura().get(i).getDescuento() / 100)));

					if(factura.getItemsFactura().get(i).getDescuento() > 0){
//						items.setPrecioUnitario(producto.getPrecioVenta() - (producto.getPrecioVenta() * (factura.getItemsFactura().get(i).getDescuento() / 100)));
//						items.setPrecioUnitario(factura.calcularDescuento(producto.getPrecioVenta()));
						items.setPrecioUnitario(factura.redondearPrecio(producto.getPrecioVenta() - (producto.getPrecioVenta() * (factura.getItemsFactura().get(i).getDescuento() / 100))));
						System.out.println(items.getPrecioUnitario());
					} else {
						items.setPrecioUnitario((double) producto.getPrecioVenta());
					}

					items.setPrecio(items.getPrecioUnitario() * items.getCantidad());
					System.out.println(items.getPrecio());
					items.setUnidadMedida("UND");
					items.setTotal(items.getPrecio() - items.getDescuento());
					System.out.println(items.getTotal());

					for (int j = 1; j <= 1; j++) {
						ImpuestosDetalle impuestos_detalle = new ImpuestosDetalle();
						impuestos_detalle.setNombreCorto("IVA");
						impuestos_detalle.setCodigoUnidadGravable(j); // Preguntar si esto varia
						impuestos_detalle.setMontoGravable((double) (items.getTotal() / 1.12));

						//impuestos_detalle.setCantidadUnidadesGravables(78.00);
						impuestos_detalle.setMontoImpuesto((double) (items.getTotal() / 1.12) * 0.12); // Preguntar sobre el calculo del impuesto
						items.setImpuestos_detalle(impuestos_detalle);
					}

					documento_fel.setItems(items);
				}

				double totalImpuestos = 0.0;

				for (int i = 0; i < documento_fel.getItems().size(); i++) {
					totalImpuestos += documento_fel.getItems().get(i).getImpuestos_detalle().get(0).getMontoImpuesto();
				}

				// CONSULTAR EL PROCEDIMIENTO EXACTO PARA LLEVAR A CABO EL CALCULO DEL RESUMEN DE IMPUESTOS
				for (int k = 1; k <= 1; k++) {
					TotalImpuestos impuestos_resumen = new TotalImpuestos();
					impuestos_resumen.setNombreCorto("IVA");
					impuestos_resumen.setTotalMontoImpuesto(totalImpuestos);
					documento_fel.setImpuestos_resumen(impuestos_resumen);
				}

				// SUMATORIA DE TODOS LOS ELEMENTOS TOTAL DE CADA UN DE LOS ITEMS
				double granTotal =  0.0;

				for (int i = 0; i < documento_fel.getItems().size(); i++) {
					granTotal += documento_fel.getItems().get(i).getTotal();
				}

				Totales totales = new Totales();
				// totales.setGranTotal(Double.parseDouble(txtTotal.getText()));
				totales.setGranTotal(granTotal);
				documento_fel.setTotales(totales);

				// Adendas
				Adendas adendas = new Adendas();
				adendas.setAdenda("Cajero", factura.getUsuario().getPrimerNombre() + " " + factura.getUsuario().getApellido());
				adendas.setAdenda("Lote", "");
				adendas.setAdenda("OrdenCompra", "");
				adendas.setAdenda("Correlativo", factura.getNoFactura().toString());

				documento_fel.setAdenda(adendas);

				//========================================================================
				// Variable para capturar la respuesta recibida del proceso de formacion del XML.
				Respuesta respuesta;

				// Objeto para enviar los datos para generacion del XML
				GenerarXml generar_xml = new GenerarXml();
				respuesta = generar_xml.ToXml(documento_fel);

				if(respuesta.getResultado()){
					FirmaEmisor firma_emisor = new FirmaEmisor();
					RespuestaServicioFirma respuesta_firma_emisor = new RespuestaServicioFirma();

					try {
						respuesta_firma_emisor = firma_emisor.Firmar(respuesta.getXml(), certificador.getAliasWs(), certificador.getTokenSigner());
					} catch (NoSuchAlgorithmException ex) {
						Logger.getLogger(FacturaApiController.class.getName()).log(Level.SEVERE, null, ex);
						response.put("message", "Ha ocurrido un error en la peticion");
						response.put("error", ex.getMessage().concat(": ").concat(ex.getCause().getMessage()));
;						return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
					} catch (UnsupportedEncodingException ex) {
						Logger.getLogger(FacturaApiController.class.getName()).log(Level.SEVERE, null, ex);
						response.put("message", "Ha ocurrido un error en la peticion");
						response.put("error", ex.getMessage().concat(": ").concat(ex.getCause().getMessage()));
					} catch (KeyManagementException ex) {
						response.put("message", "Ha ocurrido un error en la peticion");
						response.put("error", ex.getMessage().concat(": ").concat(ex.getCause().getMessage()));
						Logger.getLogger(FacturaApiController.class.getName()).log(Level.SEVERE, null, ex);
					}

					System.out.println("--> Resultado: " + respuesta_firma_emisor.isResultado());
					System.out.println("--> Descripcion: " + respuesta_firma_emisor.getDescripcion());

					if(respuesta_firma_emisor.isResultado()){
//						System.out.println("--> ENVIO AL API DE INFILE");

						ConexionServicioFel conexion = new ConexionServicioFel();
						conexion.setUrl("");
						conexion.setMetodo("POST");
						conexion.setContent_type("application/json");
						conexion.setUsuario(emisor.getNit()); // MISMO NIT
						conexion.setLlave(certificador.getLlaveWs());
						conexion.setIdentificador(factura.getNoFactura().toString() + factura.getSerie()); // DEBE VARIAR SIENDO IDENTIFICADOR UNICO

//						System.out.println("--> Enviando Documento al Servicio FEL...");

						ServicioFel servicio = new ServicioFel();

						RespuestaServicioFel respuesta_servicio = servicio.Certificar(conexion, respuesta_firma_emisor.getArchivo(), emisor.getNit(), certificador.getCorreoCopia(), "CERTIFICACION");

						if(respuesta_servicio.getResultado()){

							// INSERCIÓN DE FACTURA EN LA BASE DE DATOS DE LA EMPRESA
							factura.setEstado(estado);
							factura.setCorrelativoSat(respuesta_servicio.getNumero());
							factura.setCertificacionSat(respuesta_servicio.getUuid());
							factura.setSerieSat(respuesta_servicio.getSerie());
							factura.setMensajeSat(respuesta_servicio.getInfo());
							factura.setFechaCertificacionSat(respuesta_servicio.getFecha());
							factura.setIva(totalImpuestos);
							factura.setTipoFactura(tipoFactura);

							newFactura = serviceFactura.save(factura);

							if(newFactura != null){
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
						} else{
							// MOSTRAR ERRORES EN PANTALLA
							String errores = "";

							for (int i = 0; i < respuesta_servicio.getCantidad_errores(); i++) {
								// System.out.println(respuesta_servicio.getDescripcion_errores().get(i).getMensaje_error());
								errores += respuesta_servicio.getDescripcion_errores().get(i).getMensaje_error() + "\n";
							}
							response.put("message", "¡No se ha podido llevar a cabo la factura!");
							response.put("errores", errores);
							return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
						}
					}

				} else {

					// Ciclo para recorrer los errores.
					respuesta.getErrores().forEach((error) -> {
						System.out.println(error);
					});
				}

				/******************* PROCEDIMIENTO SIN REGIMEN FEL ******************/
//				factura.setEstado(estado);
//				newFactura = serviceFactura.save(factura);
//
//				if(newFactura != null) {
//					correlativo.setCorrelativoActual(correlativo.getCorrelativoActual() + 1);
//					serviceCorrelativo.save(correlativo);
//
//					// Actualiza el stock de los productos que forman parte de cada una de las lineas de la factura
//					for(DetalleFactura item : newFactura.getItemsFactura()) {
//						Producto producto = item.getProducto();
//						movimientoProducto = new MovimientoProducto();
//
//						movimientoProducto.setTipoMovimiento("VENTA");
//						movimientoProducto.setUsuario(factura.getUsuario());
//						movimientoProducto.setProducto(producto);
//						movimientoProducto.setStockInicial(producto.getStock());
//						movimientoProducto.setCantidad(item.getCantidad());
//						movimientoProducto.calcularStock();
//
//						serviceMovimiento.save(movimientoProducto);
//						serviceProducto.save(producto);
//					}
//				}
				/******************************************************************************/
			}

		} catch (DataAccessException e) {
			response.put("mensaje", "¡Error en la base de datos!");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			response.put("mensaje", "¡Error en la solicitud enviada!");
			response.put("error", e.getMessage().concat(": ").concat(e.getCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			response.put("mensaje", "¡Error en la solicitud enviada!");
			response.put("error", e.getMessage().concat(": ").concat(e.getCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			response.put("mensaje", "¡Error en la solicitud enviada!");
			response.put("error", e.getMessage().concat(": ").concat(e.getCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (KeyManagementException e) {
			e.printStackTrace();
			response.put("mensaje", "¡Error en la solicitud enviada!");
			response.put("error", e.getMessage().concat(": ").concat(e.getCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "¡La factura ha sido creada con éxito!");
		response.put("factura", newFactura);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@Secured(value = {"ROLE_COBRADOR", "ROLE_ADMIN"})
	@DeleteMapping(value = "/facturas/cancel/{id}/{idusuario}")
	public ResponseEntity<?> cancel(@PathVariable("id") Long idfactura, @PathVariable("idusuario") Integer idusuario){

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
			

			if(estado != null) {

				/*************** REGIMEN FEL *******************/

				emisor = serviceEmisor.getEmisor(1);
				certificador = serviceCertificador.getCertificador(1);

				// VALOR QUE REPRESENTA LA FECHA DE SOLICITUD PARA LA ANULACION DE LA FACTURA
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'-06:00'", Locale.getDefault());
				sdf.setTimeZone(TimeZone.getTimeZone("America/Guatemala"));

				// CAPTURAR FECHA DE EMISIÓN DE CERTIFICACION DE LA BASE DE DATOS
				String fechaCertificacion = "";
				fechaCertificacion = cancelFactura.getFechaCertificacionSat().replace("T", "'T'").replace("-06:00", "'-06:00'");

				SimpleDateFormat sdf_anulacion = new SimpleDateFormat(fechaCertificacion);
				sdf_anulacion.setTimeZone(TimeZone.getTimeZone("America/Guatemala"));

				AnulacionFel anulacion_fel = new AnulacionFel();

				anulacion_fel.setFechaEmisionDocumentoAnular(sdf_anulacion.format(new Date()));
				anulacion_fel.setFechaHoraAnulacion(sdf.format(new Date()));

				if(cancelFactura.getCliente().getNit().equals("C/F"))
					anulacion_fel.setIDReceptor(cancelFactura.getCliente().getNit().replace("/", ""));
				else
					anulacion_fel.setIDReceptor(cancelFactura.getCliente().getNit().replace("-", ""));

				anulacion_fel.setNITEmisor(emisor.getNit());
				anulacion_fel.setMotivoAnulacion("PRUEBA");
				anulacion_fel.setNumeroDocumentoAAnular(cancelFactura.getCertificacionSat());

				Respuesta respuesta;

				// Objeto para enviar los datos para generacion del XML
				GenerarXml generar_xml = new GenerarXml();
				respuesta = generar_xml.ToXml(anulacion_fel);
				System.out.println(respuesta.getXml());

				if(respuesta.getResultado()){

					FirmaEmisor firma_emisor = new FirmaEmisor();
					RespuestaServicioFirma respuesta_firma_emisor = new RespuestaServicioFirma();

					respuesta_firma_emisor = firma_emisor.Firmar(respuesta.getXml(), emisor.getNit(), certificador.getTokenSigner());

					if(respuesta_firma_emisor.isResultado()){

						ConexionServicioFel conexion = new ConexionServicioFel();
						conexion.setUrl("");
						conexion.setMetodo("POST");
						conexion.setContent_type("application/json");
						conexion.setUsuario(emisor.getNit());
						conexion.setLlave(certificador.getLlaveWs());
						conexion.setIdentificador("ANULACION_" + cancelFactura.getNoFactura());

						ServicioFel servicio = new ServicioFel();

						RespuestaServicioFel respuesta_servicio = servicio.Certificar(conexion, respuesta_firma_emisor.getArchivo(), emisor.getNit(), "N/A", "ANULACION");

						if(respuesta_servicio.getResultado()){
//							System.out.println("--> Resultado: " + respuesta_servicio.getResultado());
//							System.out.println("--> Origen: " + respuesta_servicio.getOrigen());
//							System.out.println("--> Descripcion: " + respuesta_servicio.getDescripcion());
//							System.out.println("--> Cantidad Errores: " + respuesta_servicio.getCantidad_errores());
//							System.out.println("--> INFO: " + respuesta_servicio.getInfo());
//
//							System.out.println("UUID: " + respuesta_servicio.getUuid());
//							System.out.println("Serie: " + respuesta_servicio.getSerie());
//							System.out.println("Numero: " + respuesta_servicio.getNumero());

							// INICIA EL PROCESO DE ANULACIÓN DE LA FACTURA EN LA BASE DE DATOS DE LA EMPRESA
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
							response.put("mensaje", "¡Petición de anulación ha salido mal");
							return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
						}
					} else {
						response.put("mensaje", "¡Petición de anulación ha salido mal");
						return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
					}
				}

				/******************* PROCEDIMIENTO SIN REGIMEN FEL ******************/

//				cancelFactura.setEstado(estado);
				
				// Recorre el listado de items de la factura y retorna al stock la cantidad comprada
//				for(DetalleFactura linea : cancelFactura.getItemsFactura()) {
//
//					Producto producto = linea.getProducto();
//					movimientoProducto = new MovimientoProducto();
//
//					movimientoProducto.setTipoMovimiento("ANULACION FACTURA");
//					movimientoProducto.setUsuario(usuario);
//
//					movimientoProducto.setProducto(producto);
//					movimientoProducto.setCantidad(linea.getCantidad());
//					movimientoProducto.calcularStock();
//
//					serviceMovimiento.save(movimientoProducto);
//					serviceProducto.save(producto);
//
//				}
//
//				serviceFactura.save(cancelFactura);
				
			} else {
				response.put("mensaje", "Estado no localizado");
				response.put("error", "El estado de anulado no pudo ser localizado");
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			}
			
		} catch (DataAccessException e) {
			response.put("mensaje", "¡Error en la base de datos!");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
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
