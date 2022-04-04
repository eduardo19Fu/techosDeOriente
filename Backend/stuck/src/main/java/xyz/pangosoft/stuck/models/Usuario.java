package xyz.pangosoft.stuck.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "usuario")
@Entity
public class Usuario {

    @Id
    private Integer idusuario;
}