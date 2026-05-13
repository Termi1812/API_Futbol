package com.apiFutbol.futbolapi.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.apiFutbol.futbolapi.model.Clasificacion;
import com.apiFutbol.futbolapi.model.Equipo;
import com.apiFutbol.futbolapi.model.Partido;
import com.apiFutbol.futbolapi.repository.ClasificacionRepository;
import com.apiFutbol.futbolapi.repository.EquipoRepository;
import com.apiFutbol.futbolapi.repository.PartidoRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SyncService {

    @Autowired
    private ClasificacionRepository clasificacionRepository;
    @Autowired
    private EquipoRepository equipoRepository;
    @Autowired
    private PartidoRepository partidoRepository;

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
    // Metodo para limpiar las tablas antes de sincronizar
    @Transactional
    private void limpiarTablas() {
        entityManager.createNativeQuery("TRUNCATE TABLE partido, clasificacion, equipo RESTART IDENTITY CASCADE").executeUpdate();
    }
    @Scheduled(
    cron = "0 0 6,18 * * *",
    zone = "Europe/Madrid"
    )// Ejecutar cada 12 horas a las 6:00 y 18:00 hora de Madrid
    @Transactional
    public void sincronizarTodo() {
        try {
            limpiarTablas();
            sincronizarEquipos();
            sincronizarClasificacion();
            sincronizarPartidos();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Transactional
    private String sincronizarEquipos() {
        try {
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

    @Transactional
    private String sincronizarClasificacion() {
        try {
            String response = webClient.get()
                    .uri("/competitions/PD/standings")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(response);
            String temporadaActual = root.get("filters").get("season").asText();
            response = webClient.get()
                    .uri("/competitions/PD/standings?season=" + temporadaActual)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            root = objectMapper.readTree(response);
            JsonNode standings = root.get("standings").get(0).get("table");

            int contador = 0;
            for (JsonNode entry : standings) {
                Long externalId = entry.get("team").get("id").asLong();

                equipoRepository.findByExternalId(externalId).ifPresent(equipo -> {
                    Clasificacion clasificacion = new Clasificacion();
                    clasificacion.setEquipo(equipo);
                    clasificacion.setPosicion(entry.get("position").asInt());
                    clasificacion.setPuntos(entry.get("points").asInt());
                    clasificacion.setPartidosJugados(entry.get("playedGames").asInt());
                    clasificacion.setPartidosGanados(entry.get("won").asInt());
                    clasificacion.setPartidosEmpatados(entry.get("draw").asInt());
                    clasificacion.setPartidosPerdidos(entry.get("lost").asInt());
                    clasificacion.setGolesFavor(entry.get("goalsFor").asInt());
                    clasificacion.setGolesContra(entry.get("goalsAgainst").asInt());
                    clasificacion.setDiferenciaGoles(entry.get("goalDifference").asInt());
                    clasificacionRepository.save(clasificacion);
                });
                contador++;
            }

            return "Clasificacion sincronizada correctamente: " + contador + " equipos";

        } catch (Exception e) {
            return "Error sincronizando clasificacion: " + e.getMessage();
        }
    }
    @Transactional
    private String sincronizarPartidos() {
        try {
            LocalDateTime desde = LocalDateTime.now().minusDays(4);
            LocalDateTime hasta = LocalDateTime.now().plusDays(4);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            String response = webClient.get()
                    .uri("/competitions/PD/matches?dateFrom=" + desde.format(fmt) + "&dateTo=" + hasta.format(fmt))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(response);
            JsonNode matches = root.get("matches");

            int contador = 0;
            for (JsonNode match : matches) {
                Long localExternalId = match.get("homeTeam").get("id").asLong();
                Long visitanteExternalId = match.get("awayTeam").get("id").asLong();

                Partido partido = new Partido();

                equipoRepository.findByExternalId(localExternalId).ifPresent(partido::setEquipoLocal);
                equipoRepository.findByExternalId(visitanteExternalId).ifPresent(partido::setEquipoVisitante);

                String fechaStr = match.get("utcDate").asText().replace("Z", "");
                partido.setFecha(LocalDateTime.parse(fechaStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME));

                // Guardar resultado si el partido está finalizado
                String status = match.get("status").asText();
                if (status.equals("FINISHED")) {
                    JsonNode fullTime = match.get("score").get("fullTime");
                    if (!fullTime.get("home").isNull() && !fullTime.get("away").isNull()) {
                        partido.setGolesLocal(fullTime.get("home").asInt());
                        partido.setGolesVisitante(fullTime.get("away").asInt());
                    }
                }

                partidoRepository.save(partido);
                contador++;
            }

            return "Partidos sincronizados correctamente: " + contador;

        } catch (Exception e) {
            return "Error sincronizando partidos: " + e.getMessage();
        }
    }
}