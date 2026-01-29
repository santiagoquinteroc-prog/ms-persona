package com.reto.ms_persona.adapters.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InscripcionResponse {
    private Long id;
    private Long personaId;
    private Long bootcampId;
    private LocalDateTime fechaInscripcion;
}

