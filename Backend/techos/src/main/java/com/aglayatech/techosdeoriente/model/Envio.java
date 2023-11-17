package com.aglayatech.techosdeoriente.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "envios")
public class Envio implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEnvio;
    private LocalDate fechaPedido;
    private String telefonoReferencia;
    private Double totalEnvio;
    private Double abono;
    private Double saldoPendiente;
    private LocalDateTime fechaRegistro;
    @NotNull(message = "La referencia no puede estar vacía.")
    private String referencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    @JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
    private Cliente cliente;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    @JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
    private Usuario usuario;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_factura")
    @JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
    private Factura factura;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_envio")
    @JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
    private List<DetalleEnvio> itemsEnvio;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "estados_envio", joinColumns = @JoinColumn(name = "id_envio"), inverseJoinColumns = @JoinColumn(name = "id_estado"))
    @JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
    private List<Estado> estados;

    public Envio() {
        this.estados = new ArrayList<>();
    }

    @PrePersist
    private void prepersist() {
        this.fechaRegistro = LocalDateTime.now();
    }

    public Integer getIdEnvio() {
        return idEnvio;
    }

    public void setIdEnvio(Integer idEnvio) {
        this.idEnvio = idEnvio;
    }

    public LocalDate getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(LocalDate fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public String getTelefonoReferencia() {
        return telefonoReferencia;
    }

    public void setTelefonoReferencia(String telefonoReferencia) {
        this.telefonoReferencia = telefonoReferencia;
    }

    public Double getTotalEnvio() {
        return totalEnvio;
    }

    public void setTotalEnvio(Double totalEnvio) {
        this.totalEnvio = totalEnvio;
    }

    public Double getAbono() {
        return abono;
    }

    public void setAbono(Double abono) {
        this.abono = abono;
    }

    public Double getSaldoPendiente() {
        return saldoPendiente;
    }

    public void setSaldoPendiente(Double saldoPendiente) {
        this.saldoPendiente = saldoPendiente;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public List<DetalleEnvio> getItemsEnvio() {
        return itemsEnvio;
    }

    public void setItemsEnvio(List<DetalleEnvio> itemsEnvio) {
        this.itemsEnvio = itemsEnvio;
    }

    public List<Estado> getEstados() {
        return estados;
    }

    public void setEstados(List<Estado> estados) {
        this.estados = estados;
    }

    /**
     * Función que determina el tipo de estados a asignar cuando se crear un nuevo envío o se genera un movimiento
     * sobre el mismo.
     * @param estados Listado de estados disponibles en la base de datos para poder asignar
     * @return Devuelve el listado de estados para asignar al envío analizado.
     * */
    public List<Estado> determinarEstadosEnvio(List<Estado> estados) {
        List<Estado> newEstados = new ArrayList<>();

        if (abono <= 0 && totalEnvio > 0.0) {
            for(Estado estado : estados) {
                if (estado.getEstado().equals("pendiente".toUpperCase()) || estado.getEstado().equals("pagado".toUpperCase())) {
                    newEstados.add(estado);
                }
            }
        } else if (abono > 0.0) {
            for (Estado estado : estados) {
                if (estado.getEstado().equals("pendiente".toUpperCase()) || estado.getEstado().equals("no pagado".toUpperCase())) {
                    newEstados.add(estado);
                }
            }
        }

        return newEstados;
    }

    public static List<Estado> determinarEstadosEnvio(int movimiento, List<Estado> estados) {
        // movimiento 1 = DESPACHAR SOLO SI YA ESTA PAGADO O ABONO ES MAYOR QUE CERO
        // movimiento 2 = COMPLETAR PAGO SI ABONO ES MAYOR A CERO Y DESPACHAR

        List<Estado> newEstados = new ArrayList<>();

        switch (movimiento) {
            case 1:
                newEstados = estados.stream()
                                .filter((estado -> estado.getEstado().equals("despachado".toUpperCase()) || estado.getEstado().equals("no pagado".toUpperCase())))
                                .map(estado -> estado)
                                .collect(Collectors.toList());
                break;
            case 2:
                newEstados = estados
                                .stream()
                                .filter((estado -> estado.getEstado().equals("despachado".toUpperCase()) || estado.getEstado().equals("pagado".toUpperCase())))
                                .map(estado -> estado)
                                .collect(Collectors.toList());
                break;
            case 3:
                newEstados = estados
                        .stream()
                        .filter((estado -> estado.getEstado().equals("cancelado".toUpperCase()) || estado.getEstado().equals("no pagado".toUpperCase())))
                        .map(estado -> estado)
                        .collect(Collectors.toList());
                break;
            default:
                break;
        }

        return newEstados;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Envio{");
        sb.append("idEnvio=").append(idEnvio);
        sb.append(", fechaPedido=").append(fechaPedido);
        sb.append(", telefonoReferencia='").append(telefonoReferencia).append('\'');
        sb.append(", totalEnvio=").append(totalEnvio);
        sb.append(", abono=").append(abono);
        sb.append(", saldoPendiente=").append(saldoPendiente);
        sb.append(", fechaRegistro=").append(fechaRegistro);
        sb.append(", referencia='").append(referencia).append('\'');
        sb.append(", cliente=").append(cliente);
        sb.append(", usuario=").append(usuario);
        sb.append(", itemsEnvio=").append(itemsEnvio);
        sb.append(", estados=").append(estados);
        sb.append(", factura=").append(factura);
        sb.append('}');
        return sb.toString();
    }

    private final static long serialVersionUID = 1L;
}
