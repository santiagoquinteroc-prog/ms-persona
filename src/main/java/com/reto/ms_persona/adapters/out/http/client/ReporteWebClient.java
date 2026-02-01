package com.reto.ms_persona.adapters.out.http.client;

import com.reto.ms_persona.application.ports.output.ReporteServicePort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@Slf4j
public class ReporteWebClient implements ReporteServicePort {

    private final WebClient reporteWebClient;

    public ReporteWebClient(@Qualifier("reporteServiceWebClient") WebClient reporteWebClient) {
        this.reporteWebClient = reporteWebClient;
    }

    @Override
    public Mono<Void> notificarInscripcion(Long bootcampId, String nombre, String correo) {
        Map<String, String> body = Map.of(
                "nombre", nombre,
                "correo", correo
        );

        return reporteWebClient.post()
                .uri("/reportes/bootcamps/{bootcampId}/inscripciones", bootcampId)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(result -> log.info("Inscripción notificada a ms-reporte: bootcampId={}, nombre={}, correo={}", bootcampId, nombre, correo))
                .doOnError(error -> log.error("Error al notificar inscripción a ms-reporte: bootcampId={}, nombre={}, correo={}, error={}", bootcampId, nombre, correo, error.getMessage()))
                .onErrorResume(error -> {
                    log.warn("No se pudo notificar inscripción a ms-reporte, pero la inscripción se guardó correctamente");
                    return Mono.empty();
                })
                .then();
    }
}

