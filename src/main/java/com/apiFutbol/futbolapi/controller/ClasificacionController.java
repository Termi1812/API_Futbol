package com.apiFutbol.futbolapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.apiFutbol.futbolapi.model.Clasificacion;
import com.apiFutbol.futbolapi.service.ClasificacionService;

@RestController
@RequestMapping("/api/clasificacion")
public class ClasificacionController {

    @Autowired
    private ClasificacionService clasificacionService;

    @GetMapping("/equipo/tabla")
    public List<Clasificacion> obtenerTabla() {
        return clasificacionService.obtenerTabla();
    }

    @GetMapping("/equipo/{equipoId}")
    public ResponseEntity<Clasificacion> obtenerPosicionEquipo(
            @PathVariable Long equipoId,
            @RequestParam String temporada) {
        return clasificacionService.obtenerPosicionEquipo(equipoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}