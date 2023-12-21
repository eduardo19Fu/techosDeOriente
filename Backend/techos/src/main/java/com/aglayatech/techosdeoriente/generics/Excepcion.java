package com.aglayatech.techosdeoriente.generics;

import org.springframework.dao.DataAccessException;

import java.util.HashMap;
import java.util.Map;

public class Excepcion {

    /**
     * Manejador de Excepciones generales para los errores ocurridos en el servidor al momento de llevar a cabo la persistencia.
     * @param e Parámetro de tipo DataAccessException para evaluar.
     * @return Devuelve una respuesta de tipo Map<String, Object>
     * */
    public static Map<String, Object> dataAccessExceptionHandler(DataAccessException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Ha ocurrido un error en la base de datos.");
        response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));

        return response;
    }
}
