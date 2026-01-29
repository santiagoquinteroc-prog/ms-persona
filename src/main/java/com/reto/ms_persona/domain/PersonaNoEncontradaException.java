package com.reto.ms_persona.domain;

public class PersonaNoEncontradaException extends RuntimeException {
    public PersonaNoEncontradaException(String message) {
        super(message);
    }
}

