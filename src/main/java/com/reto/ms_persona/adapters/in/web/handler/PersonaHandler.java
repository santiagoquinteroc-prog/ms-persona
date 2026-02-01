package com.reto.ms_persona.adapters.in.web.handler;

import com.reto.ms_persona.adapters.in.web.dto.CreatePersonaRequest;
import com.reto.ms_persona.adapters.in.web.dto.InscribirPersonaRequest;
import com.reto.ms_persona.adapters.in.web.mapper.PersonaMapper;
import com.reto.ms_persona.application.ports.input.CrearPersonaUseCase;
import com.reto.ms_persona.application.ports.input.InscribirPersonaUseCase;
import com.reto.ms_persona.application.ports.output.PersonaRepositoryPort;
import com.reto.ms_persona.domain.BootcampNoEncontradoException;
import com.reto.ms_persona.domain.BootcampSolapadoException;
import com.reto.ms_persona.domain.CorreoDuplicadoException;
import com.reto.ms_persona.domain.MaximoBootcampsException;
import com.reto.ms_persona.domain.PersonaNoEncontradaException;
import com.reto.ms_persona.domain.PersonaYaInscritaException;
import jakarta.validation.Validator;
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
    private final CrearPersonaUseCase crearPersonaUseCase;
    private final PersonaRepositoryPort personaRepositoryPort;
    private final PersonaMapper personaMapper;
    private final Validator validator;

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
                    .onErrorResume(BootcampNoEncontradoException.class, ex -> ServerResponse
                            .status(HttpStatus.NOT_FOUND)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ex.getMessage()))
                    .onErrorResume(PersonaYaInscritaException.class, ex -> ServerResponse
                            .status(HttpStatus.CONFLICT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ex.getMessage()))
                    .onErrorResume(MaximoBootcampsException.class, ex -> ServerResponse
                            .status(HttpStatus.CONFLICT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ex.getMessage()))
                    .onErrorResume(BootcampSolapadoException.class, ex -> ServerResponse
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

    public Mono<ServerResponse> crearPersona(ServerRequest request) {
        return request.bodyToMono(CreatePersonaRequest.class)
                .flatMap(body -> {
                    var violations = validator.validate(body);
                    if (!violations.isEmpty()) {
                        String errorMessage = violations.stream()
                                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                                .reduce((a, b) -> a + "; " + b)
                                .orElse("Error de validación");
                        return Mono.error(new IllegalArgumentException(errorMessage));
                    }
                    return crearPersonaUseCase.ejecutar(body.getNombre(), body.getCorreo())
                            .map(personaMapper::toResponse)
                            .flatMap(response -> ServerResponse
                                    .status(HttpStatus.CREATED)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(response))
                            .onErrorResume(CorreoDuplicadoException.class, ex -> ServerResponse
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
                });
    }

    public Mono<ServerResponse> obtenerPersona(ServerRequest request) {
        try {
            Long personaId = Long.parseLong(request.pathVariable("id"));
            return personaRepositoryPort.findById(personaId)
                    .map(personaMapper::toResponse)
                    .flatMap(response -> ServerResponse
                            .status(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(response))
                    .switchIfEmpty(ServerResponse
                            .status(HttpStatus.NOT_FOUND)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue("Persona con ID " + personaId + " no encontrada"))
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

    public Mono<ServerResponse> listarPersonas(ServerRequest request) {
        return personaRepositoryPort.findAll()
                .map(personaMapper::toResponse)
                .collectList()
                .flatMap(personas -> ServerResponse
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(personas))
                .onErrorResume(error -> ServerResponse
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(error.getMessage()));
    }
}

