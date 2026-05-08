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

    @PostMapping("/todo")
    public String sincronizarTodo() {
        try {
            syncService.sincronizarTodo();
            return "Sincronización completa";
        } catch (Exception e) {
            return "Error durante la sincronización: " + e.getMessage();
        }
    }
}