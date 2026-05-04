package com.apiFutbol.futbolapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.apiFutbol.futbolapi.model.Equipo;
import com.apiFutbol.futbolapi.repository.EquipoRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SyncService {

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SyncService(@Value("${football.api.url}") String apiUrl,
                       @Value("${football.api.token}") String apiToken) {
        this.webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("X-Auth-Token", apiToken)
                .build();
    }

    private void resetSequence() {
        entityManager.createNativeQuery("ALTER SEQUENCE equipo_id_seq RESTART WITH 1").executeUpdate();
    }

    @Transactional
    public String sincronizarEquipos() {
        try {
            equipoRepository.deleteAll();
            resetSequence();

            String response = webClient.get()
                    .uri("/competitions/PD/teams")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(response);
            JsonNode teams = root.get("teams");

            int contador = 0;
            for (JsonNode team : teams) {
                Long externalId = team.get("id").asLong();

                Equipo equipo = equipoRepository.findByExternalId(externalId)
                        .orElse(new Equipo());

                equipo.setExternalId(externalId);
                equipo.setNombre(team.get("name").asText());
                equipo.setEscudo(team.has("crest") ? team.get("crest").asText() : "Desconocido");
                equipo.setEstadio(team.has("venue") ? team.get("venue").asText() : "Desconocido");

                equipoRepository.save(equipo);
                contador++;
            }

            return "Equipos sincronizados correctamente: " + contador;

        } catch (Exception e) {
            return "Error sincronizando equipos: " + e.getMessage();
        }
    }
}