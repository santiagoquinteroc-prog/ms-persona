package com.reto.ms_persona.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Persona {
    private Long id;
    private String nombre;
    private String correo;
}

