package com.apiFutbol.futbolapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apiFutbol.futbolapi.model.Clasificacion;
import com.apiFutbol.futbolapi.repository.ClasificacionRepository;

@Service
public class ClasificacionService {

    @Autowired
    private ClasificacionRepository clasificacionRepository;

    public List<Clasificacion> obtenerTabla() {
        return clasificacionRepository.obtenerTabla();
    }

    public Optional<Clasificacion> obtenerPosicionEquipo(Long equipoId) {
        return clasificacionRepository.obtenerPosicionEquipo(equipoId);
    }
}