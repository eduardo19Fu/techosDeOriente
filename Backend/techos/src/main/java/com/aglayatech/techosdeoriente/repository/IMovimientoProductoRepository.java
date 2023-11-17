package com.aglayatech.techosdeoriente.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.aglayatech.techosdeoriente.model.TipoMovimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.aglayatech.techosdeoriente.model.MovimientoProducto;
import com.aglayatech.techosdeoriente.model.Producto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IMovimientoProductoRepository extends JpaRepository<MovimientoProducto, Long> {
	
	Page<MovimientoProducto> findByProducto(Producto producto, Pageable pageable);
	
	// Movimientos listados por rango de fechas
	List<MovimientoProducto> findByFechaMovimientoBetween(Date fechaIni, Date fechaFin);

	@Query(value = "Select tm from TipoMovimiento tm where tm.tipoMovimiento = :nombre")
	Optional<TipoMovimiento> findTipoMovimientoByNombre(@Param("nombre") String nombre);

	@Query(value = "Select tm from TipoMovimiento tm")
	List<TipoMovimiento> findTiposMovimiento();

}
