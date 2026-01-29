package com.reto.ms_persona.adapters.in.web.router;

import com.reto.ms_persona.adapters.in.web.dto.CreatePersonaRequest;
import com.reto.ms_persona.adapters.in.web.dto.InscribirPersonaRequest;
import com.reto.ms_persona.adapters.in.web.dto.InscripcionResponse;
import com.reto.ms_persona.adapters.in.web.dto.PersonaResponse;
import com.reto.ms_persona.adapters.in.web.handler.PersonaHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class PersonaRouter {

    @Bean
    @RouterOperations({
        @RouterOperation(
            path = "/personas",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            method = RequestMethod.POST,
            beanClass = PersonaHandler.class,
            beanMethod = "crearPersona",
            operation = @Operation(
                operationId = "crearPersona",
                summary = "Crear persona",
                tags = {"Personas"},
                requestBody = @RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreatePersonaRequest.class))
                ),
                responses = {
                    @ApiResponse(responseCode = "201", description = "Persona creada",
                        content = @Content(schema = @Schema(implementation = PersonaResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
                    @ApiResponse(responseCode = "409", description = "Correo duplicado")
                }
            )
        ),
        @RouterOperation(
            path = "/personas/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            method = RequestMethod.GET,
            beanClass = PersonaHandler.class,
            beanMethod = "obtenerPersona",
            operation = @Operation(
                operationId = "obtenerPersona",
                summary = "Obtener persona por ID",
                tags = {"Personas"},
                responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                        content = @Content(schema = @Schema(implementation = PersonaResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Persona no encontrada"),
                    @ApiResponse(responseCode = "400", description = "ID inválido")
                }
            )
        ),
        @RouterOperation(
            path = "/personas/{personaId}/inscripciones",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            method = RequestMethod.POST,
            beanClass = PersonaHandler.class,
            beanMethod = "inscribirPersona",
            operation = @Operation(
                operationId = "inscribirPersona",
                summary = "Inscribir persona en bootcamp",
                tags = {"Inscripciones"},
                requestBody = @RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = InscribirPersonaRequest.class))
                ),
                responses = {
                    @ApiResponse(responseCode = "201", description = "Inscripción creada",
                        content = @Content(schema = @Schema(implementation = InscripcionResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
                    @ApiResponse(responseCode = "404", description = "Persona o bootcamp no encontrado"),
                    @ApiResponse(responseCode = "409", description = "Persona ya inscrita, máximo bootcamps alcanzado o bootcamp solapado")
                }
            )
        )
    })
    public RouterFunction<ServerResponse> personaRoutes(PersonaHandler personaHandler) {
        return RouterFunctions.route(POST("/personas").and(accept(org.springframework.http.MediaType.APPLICATION_JSON)), personaHandler::crearPersona)
                .andRoute(GET("/personas/{id}"), personaHandler::obtenerPersona)
                .andRoute(POST("/personas/{personaId}/inscripciones").and(accept(org.springframework.http.MediaType.APPLICATION_JSON)), personaHandler::inscribirPersona);
    }
}

