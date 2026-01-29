CREATE DATABASE IF NOT EXISTS persona_db;
USE persona_db;

CREATE TABLE IF NOT EXISTS persona (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL,
    correo VARCHAR(120) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS inscripcion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    persona_id BIGINT NOT NULL,
    bootcamp_id BIGINT NOT NULL,
    fecha_inscripcion DATETIME NOT NULL,
    CONSTRAINT fk_inscripcion_persona FOREIGN KEY (persona_id) REFERENCES persona(id),
    CONSTRAINT uk_persona_bootcamp UNIQUE (persona_id, bootcamp_id)
);

