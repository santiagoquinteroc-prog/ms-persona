package com.reto.ms_persona.adapters.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePersonaRequest {
    @NotBlank(message = "El nombre es requerido")
    private String nombre;

    @NotBlank(message = "El correo es requerido")
    @Email(message = "El correo debe tener un formato válido")
    private String correo;
}

