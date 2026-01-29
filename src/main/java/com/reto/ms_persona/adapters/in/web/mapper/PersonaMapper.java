package com.reto.ms_persona.adapters.in.web.mapper;

import com.reto.ms_persona.adapters.in.web.dto.InscripcionResponse;
import com.reto.ms_persona.adapters.in.web.dto.PersonaResponse;
import com.reto.ms_persona.domain.Inscripcion;
import com.reto.ms_persona.domain.Persona;
import org.springframework.stereotype.Component;

@Component
public class PersonaMapper {
    public InscripcionResponse toResponse(Inscripcion inscripcion) {
        return InscripcionResponse.builder()
                .id(inscripcion.getId())
                .personaId(inscripcion.getPersonaId())
                .bootcampId(inscripcion.getBootcampId())
                .fechaInscripcion(inscripcion.getFechaInscripcion())
                .build();
    }

    public PersonaResponse toResponse(Persona persona) {
        return PersonaResponse.builder()
                .id(persona.getId())
                .nombre(persona.getNombre())
                .correo(persona.getCorreo())
                .build();
    }
}

