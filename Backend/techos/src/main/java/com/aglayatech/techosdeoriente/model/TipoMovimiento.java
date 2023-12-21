package com.aglayatech.techosdeoriente.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "tipos_movimiento")
public class TipoMovimiento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTipoMovimiento;
    private String tipoMovimiento;

    public Integer getIdTipoMovimiento() {
        return idTipoMovimiento;
    }

    public void setIdTipoMovimiento(Integer idTipoMovimiento) {
        this.idTipoMovimiento = idTipoMovimiento;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    private static final long serialVersionUID = 1L;
}
