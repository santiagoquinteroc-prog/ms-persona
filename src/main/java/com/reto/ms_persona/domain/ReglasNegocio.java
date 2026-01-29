package com.reto.ms_persona.domain;

import java.time.LocalDate;
import java.util.List;

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

    public static void validarMaximoBootcamps(List<Inscripcion> inscripciones, BootcampInfo nuevoBootcamp) {
        LocalDate inicioNuevo = nuevoBootcamp.getFechaLanzamiento();
        LocalDate finNuevo = inicioNuevo.plusDays(nuevoBootcamp.getDuracionSemanas() * 7L);

        long bootcampsSimultaneos = inscripciones.stream()
                .filter(inscripcion -> {
                    LocalDate inicioExistente = inscripcion.getFechaInicio();
                    LocalDate finExistente = inscripcion.getFechaFin();
                    return seSolapan(inicioNuevo, finNuevo, inicioExistente, finExistente);
                })
                .count();

        if (bootcampsSimultaneos >= 5) {
            throw new MaximoBootcampsException("No se puede inscribir. Ya tiene 5 bootcamps simultáneos");
        }
    }

    public static void validarNoSolape(List<Inscripcion> inscripciones, BootcampInfo nuevoBootcamp) {
        LocalDate inicioNuevo = nuevoBootcamp.getFechaLanzamiento();
        LocalDate finNuevo = inicioNuevo.plusDays(nuevoBootcamp.getDuracionSemanas() * 7L);

        boolean haySolape = inscripciones.stream()
                .anyMatch(inscripcion -> {
                    LocalDate inicioExistente = inscripcion.getFechaInicio();
                    LocalDate finExistente = inscripcion.getFechaFin();
                    return seSolapan(inicioNuevo, finNuevo, inicioExistente, finExistente);
                });

        if (haySolape) {
            throw new BootcampSolapadoException("El bootcamp se cruza con otro bootcamp ya inscrito");
        }
    }

    private static boolean seSolapan(LocalDate inicio1, LocalDate fin1, LocalDate inicio2, LocalDate fin2) {
        return inicio1.isBefore(fin2) && inicio2.isBefore(fin1);
    }
}

