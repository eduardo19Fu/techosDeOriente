CREATE FUNCTION get_mes (mes int)
RETURNS TEXT
BEGIN
	DECLARE NOMBRE_MES VARCHAR(100);
    
    SELECT CASE
		WHEN mes = 1 THEN 'ENERO'
        WHEN mes = 2 THEN 'FEBRERO'
        WHEN mes = 3 THEN 'MARZO'
        WHEN mes = 4 THEN 'ABRIL'
        WHEN mes = 5 THEN 'MAYO'
        WHEN mes = 6 THEN 'JUNIO'
        WHEN mes = 7 THEN 'JULIO'
        WHEN mes = 8 THEN 'AGOSTO'
        WHEN mes = 9 THEN 'SEPTIEMBRE'
        WHEN mes = 10 THEN 'OCTUBRE'
        WHEN mes = 11 THEN 'NOVIEMBRE'
        WHEN mes = 12 THEN 'DICIEMBRE'
        ELSE ''
	END
    INTO NOMBRE_MES;
RETURN NOMBRE_MES;
END
