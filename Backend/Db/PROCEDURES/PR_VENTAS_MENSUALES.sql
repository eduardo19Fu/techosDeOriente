CREATE DEFINER=`root`@`localhost` PROCEDURE `PR_VENTAS_MENSUALES`(V_YEAR INT)
BEGIN
	SELECT get_mes(MONTH(fecha)) AS MES, SUM(total) AS TOTAL
    FROM facturas 
    WHERE YEAR(fecha) = V_YEAR
    GROUP BY MONTH(fecha);
END