package com.reto.ms_persona.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inscripcion {
    private Long id;
    private Long personaId;
    private Long bootcampId;
    private LocalDateTime fechaInscripcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}

