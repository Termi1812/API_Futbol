package com.apiFutbol.futbolapi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.apiFutbol.futbolapi.model.Equipo;

@Repository
public interface EquipoRepository extends JpaRepository<Equipo, Long> {
    @Query("SELECT e FROM Equipo e WHERE LOWER(e.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Equipo> buscarPorNombre(@Param("nombre") String nombre);
    @Query("SELECT e FROM Equipo e WHERE e.externalId = :externalId")
    Optional<Equipo> findByExternalId(@Param("externalId") Long externalId);
}