package com.aglayatech.techosdeoriente.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "tipos_comprobante")
public class TipoComprobante implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTipoComprobante;
    @NotNull(message = "Tipo de Comprobante no puede estar vacío.")
    private String tipoComprobante;

    public Integer getIdTipoComprobante() {
        return idTipoComprobante;
    }

    public void setIdTipoComprobante(Integer idTipoComprobante) {
        this.idTipoComprobante = idTipoComprobante;
    }

    public String getTipoComprobante() {
        return tipoComprobante;
    }

    public void setTipoComprobante(String tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    private static final long serialVersionUID = 1L;
}
