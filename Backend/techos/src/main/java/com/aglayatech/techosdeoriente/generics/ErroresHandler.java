package com.aglayatech.techosdeoriente.generics;

import org.springframework.validation.BindingResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ErroresHandler {


    /**
     * Función que evalúa los errores obtenidos por validación del lado del backend
     * @param result Parámetro que tiene los resultados de la validación.
     * @return Devuelve un Map con los resultados obtenidos.
     * */
    public static Map<String, Object> bingingResultErrorsHandler(BindingResult result) {
        Map<String, Object> response = new HashMap<>();

        List<String> errores =  result.getFieldErrors().stream()
                                .map(err -> "El campo '".concat(err.getField().concat("' ")).concat(err.getDefaultMessage()))
                                .collect(Collectors.toList());

        response.put("errores", errores);
        return response;
    }
}
