package com.apiFutbol.futbolapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.apiFutbol.futbolapi.model.Clasificacion;
import com.apiFutbol.futbolapi.repository.ClasificacionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ClasificacionService {

    @Autowired
    private ClasificacionRepository clasificacionRepository;

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ClasificacionService(@Value("${football.api.url}") String apiUrl,
                       @Value("${football.api.token}") String apiToken) {
        this.webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("X-Auth-Token", apiToken)
                .build();
    }

    public List<Clasificacion> obtenerTabla() {
        return clasificacionRepository.obtenerTabla();
    }

    public Optional<Clasificacion> obtenerPosicionEquipo(Long equipoId) {
        return clasificacionRepository.obtenerPosicionEquipo(equipoId);
    }

    public Optional<String> obtenerTemporadaActual() {
        try {
            String response = webClient.get()
                    .uri("/competitions/PD/standings")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(response);
            String temporadaActual = root.get("filters").get("season").asText();
            String temporadaFormateada = temporadaActual + "/" + (Integer.parseInt(temporadaActual) + 1);
            return Optional.ofNullable(temporadaFormateada);
        } catch (Exception e) {
            System.err.println("Error al obtener la temporada actual: " + e.getMessage());
            return Optional.empty();
        }
    }
}