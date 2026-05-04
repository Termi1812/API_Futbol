package com.apiFutbol.futbolapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apiFutbol.futbolapi.service.SyncService;

@RestController
@RequestMapping("/api/sync")
public class SyncController {

    @Autowired
    private SyncService syncService;

    @PostMapping("/equipos")
    public String sincronizarEquipos() {
        return syncService.sincronizarEquipos();
    }

    @PostMapping("/clasificacion")
    public String sincronizarClasificacion() {
        return syncService.sincronizarClasificacion();
    }
    @PostMapping("/partidos")
    public String sincronizarPartidos() {
        return syncService.sincronizarPartidos();
    }
}
