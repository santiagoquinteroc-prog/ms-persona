package com.reto.ms_persona.domain;

public class ReglasNegocio {
    public static void validarInscripcion(Inscripcion inscripcion) {
        if (inscripcion == null) {
            throw new IllegalArgumentException("La inscripción no puede ser nula");
        }
        if (inscripcion.getPersonaId() == null) {
            throw new IllegalArgumentException("El ID de persona es requerido");
        }
        if (inscripcion.getBootcampId() == null) {
            throw new IllegalArgumentException("El ID de bootcamp es requerido");
        }
    }
}

