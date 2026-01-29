package com.reto.ms_persona.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BootcampInfo {
    private Long id;
    private LocalDate fechaLanzamiento;
    private Integer duracionSemanas;
}

