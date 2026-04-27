package com.apiFutbol.futbolapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apiFutbol.futbolapi.model.Partido;
import com.apiFutbol.futbolapi.service.PartidoService;

@RestController
@RequestMapping("/api/partidos")
public class PartidoController {

    @Autowired
    private PartidoService partidoService;

    @GetMapping("/proximos")
    public List<Partido> obtenerProximos() {
        return partidoService.obtenerProximos();
    }

    @GetMapping("/resultados")
    public List<Partido> obtenerResultados() {
        return partidoService.obtenerResultados();
    }

    @GetMapping("/equipo/{equipoId}")
    public List<Partido> obtenerPartidosEquipo(@PathVariable Long equipoId) {
        return partidoService.obtenerPartidosEquipo(equipoId);
    }
}
