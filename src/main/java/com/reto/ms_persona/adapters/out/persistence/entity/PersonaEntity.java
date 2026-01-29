package com.reto.ms_persona.adapters.out.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("persona")
public class PersonaEntity {
    @Id
    private Long id;
    
    @Column("nombre")
    private String nombre;
    
    @Column("correo")
    private String correo;
}

