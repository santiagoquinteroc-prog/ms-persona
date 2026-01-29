package com.reto.ms_persona.adapters.out.http.client;

import com.reto.ms_persona.application.ports.output.BootcampServicePort;
import com.reto.ms_persona.domain.BootcampInfo;
import com.reto.ms_persona.domain.BootcampNoEncontradoException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class BootcampWebClient implements BootcampServicePort {

    private final WebClient bootcampWebClient;

    public BootcampWebClient(@Qualifier("bootcampServiceWebClient") WebClient bootcampWebClient) {
        this.bootcampWebClient = bootcampWebClient;
    }

    @Override
    public Mono<BootcampInfo> findById(Long id) {
        return bootcampWebClient.get()
                .uri("/bootcamps?size=1000")
                .retrieve()
                .onStatus(status -> status == HttpStatus.NOT_FOUND, response -> 
                    Mono.error(new BootcampNoEncontradoException("Bootcamp con ID " + id + " no encontrado")))
                .bodyToMono(Map.class)
                .flatMap(response -> {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");
                    if (items == null || items.isEmpty()) {
                        return Mono.error(new BootcampNoEncontradoException("Bootcamp con ID " + id + " no encontrado"));
                    }
                    return items.stream()
                            .filter(item -> {
                                Object itemId = item.get("id");
                                return itemId != null && Long.valueOf(itemId.toString()).equals(id);
                            })
                            .findFirst()
                            .map(item -> {
                                LocalDate fechaLanzamiento = LocalDate.parse(item.get("fechaLanzamiento").toString());
                                Integer duracionSemanas = Integer.valueOf(item.get("duracionSemanas").toString());
                                return BootcampInfo.builder()
                                        .id(id)
                                        .fechaLanzamiento(fechaLanzamiento)
                                        .duracionSemanas(duracionSemanas)
                                        .build();
                            })
                            .map(Mono::just)
                            .orElse(Mono.error(new BootcampNoEncontradoException("Bootcamp con ID " + id + " no encontrado")));
                })
                .onErrorResume(error -> {
                    if (error instanceof BootcampNoEncontradoException) {
                        return Mono.error(error);
                    }
                    return Mono.error(new BootcampNoEncontradoException("Error al consultar bootcamp: " + error.getMessage()));
                });
    }
}

