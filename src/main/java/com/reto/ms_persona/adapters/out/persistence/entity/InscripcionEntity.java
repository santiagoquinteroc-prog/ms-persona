package com.reto.ms_persona.adapters.out.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("inscripcion")
public class InscripcionEntity {
    @Id
    private Long id;
    
    @Column("persona_id")
    private Long personaId;
    
    @Column("bootcamp_id")
    private Long bootcampId;
    
    @Column("fecha_inscripcion")
    private LocalDateTime fechaInscripcion;
}

