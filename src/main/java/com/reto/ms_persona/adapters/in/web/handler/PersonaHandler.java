package com.reto.ms_persona.adapters.in.web.handler;

import com.reto.ms_persona.adapters.in.web.dto.InscribirPersonaRequest;
import com.reto.ms_persona.adapters.in.web.mapper.PersonaMapper;
import com.reto.ms_persona.application.ports.input.InscribirPersonaUseCase;
import com.reto.ms_persona.domain.PersonaNoEncontradaException;
import com.reto.ms_persona.domain.PersonaYaInscritaException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PersonaHandler {

    private final InscribirPersonaUseCase inscribirPersonaUseCase;
    private final PersonaMapper personaMapper;

    public Mono<ServerResponse> inscribirPersona(ServerRequest request) {
        try {
            Long personaId = Long.parseLong(request.pathVariable("personaId"));
            
            return request.bodyToMono(InscribirPersonaRequest.class)
                    .flatMap(req -> inscribirPersonaUseCase.ejecutar(personaId, req.getBootcampId()))
                    .map(personaMapper::toResponse)
                    .flatMap(response -> ServerResponse
                            .status(HttpStatus.CREATED)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(response))
                    .onErrorResume(PersonaNoEncontradaException.class, ex -> ServerResponse
                            .status(HttpStatus.NOT_FOUND)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ex.getMessage()))
                    .onErrorResume(PersonaYaInscritaException.class, ex -> ServerResponse
                            .status(HttpStatus.CONFLICT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ex.getMessage()))
                    .onErrorResume(IllegalArgumentException.class, ex -> ServerResponse
                            .status(HttpStatus.BAD_REQUEST)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ex.getMessage()))
                    .onErrorResume(error -> ServerResponse
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(error.getMessage()));
        } catch (NumberFormatException ex) {
            return ServerResponse
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("ID de persona inválido");
        }
    }
}

