package com.aglayatech.licorstore.generics;

import org.springframework.dao.DataAccessException;

import java.util.HashMap;
import java.util.Map;

public class Excepcion {

    public static Map<String, Object> dataAccessExceptionHandler(DataAccessException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Ha ocurrido un error en la base de datos.");
        response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));

        return response;
    }
}
