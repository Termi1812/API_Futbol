package com.apiFutbol.futbolapi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.apiFutbol.futbolapi.model.Clasificacion;

@Repository
public interface ClasificacionRepository extends JpaRepository<Clasificacion, Long> {

    @Query("SELECT c FROM Clasificacion c ORDER BY c.posicion ASC")
    List<Clasificacion> obtenerTabla();

    @Query("SELECT c FROM Clasificacion c WHERE c.equipo.id = :equipoId")
    Optional<Clasificacion> obtenerPosicionEquipo(@Param("equipoId") Long equipoId);
}