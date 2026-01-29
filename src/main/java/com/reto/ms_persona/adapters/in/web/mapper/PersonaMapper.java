package com.reto.ms_persona.adapters.in.web.mapper;

import com.reto.ms_persona.adapters.in.web.dto.InscripcionResponse;
import com.reto.ms_persona.domain.Inscripcion;
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
}

