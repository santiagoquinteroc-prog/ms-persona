package com.reto.ms_persona.adapters.in.web.router;

import com.reto.ms_persona.adapters.in.web.handler.PersonaHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class PersonaRouter {

    @Bean
    public RouterFunction<ServerResponse> personaRoutes(PersonaHandler personaHandler) {
        return RouterFunctions.route(POST("/personas").and(accept(org.springframework.http.MediaType.APPLICATION_JSON)), personaHandler::crearPersona)
                .andRoute(GET("/personas/{id}"), personaHandler::obtenerPersona)
                .andRoute(POST("/personas/{personaId}/inscripciones").and(accept(org.springframework.http.MediaType.APPLICATION_JSON)), personaHandler::inscribirPersona);
    }
}

