package com.apiFutbol.futbolapi.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.apiFutbol.futbolapi.model.Partido;

@Repository
public interface PartidoRepository extends JpaRepository<Partido, Long> {

    @Query("SELECT p FROM Partido p WHERE p.equipoLocal.id = :equipoId OR p.equipoVisitante.id = :equipoId ORDER BY p.fecha ASC")
    List<Partido> obtenerPartidosEquipo(@Param("equipoId") Long equipoId);

    @Query("SELECT p FROM Partido p WHERE p.fecha > :fecha ORDER BY p.fecha ASC")
    List<Partido> obtenerProximos(@Param("fecha") LocalDateTime fecha);

    @Query("SELECT p FROM Partido p WHERE p.fecha < :fecha ORDER BY p.fecha DESC")
    List<Partido> obtenerResultados(@Param("fecha") LocalDateTime fecha);
}