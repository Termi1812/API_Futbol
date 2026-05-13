package com.apiFutbol.futbolapi.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apiFutbol.futbolapi.model.Partido;
import com.apiFutbol.futbolapi.repository.PartidoRepository;

@Service
public class PartidoService {

    @Autowired
    private PartidoRepository partidoRepository;

    public List<Partido> obtenerPartidosEquipo(Long equipoId) {
        return partidoRepository.obtenerPartidosEquipo(equipoId);
    }

    public List<Partido> obtenerProximos() {
        return partidoRepository.obtenerProximos(LocalDateTime.now());
    }

    public List<Partido> obtenerResultados() {
        return partidoRepository.obtenerResultados(LocalDateTime.now());
    }
    public List<Partido> obtenerPartidosProximosEquipo(Long equipoId) {
        return partidoRepository.obtenerPartidosProximosEquipo(equipoId, LocalDateTime.now());
    }
}