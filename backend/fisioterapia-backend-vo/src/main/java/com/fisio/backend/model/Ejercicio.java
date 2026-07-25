package com.fisio.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "ejercicios")
public class Ejercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    private String nombre;

    @NotBlank
    @Size(max = 500)
    private String descripcion;

    @NotNull
    @Column(name = "angulo_objetivo")
    private Integer anguloObjetivo; // Ángulo objetivo del ejercicio (ej. 90 para sentadillas)

    // Constructores
    public Ejercicio() {}

    public Ejercicio(String nombre, String descripcion, Integer anguloObjetivo) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.anguloObjetivo = anguloObjetivo;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getAnguloObjetivo() {
        return anguloObjetivo;
    }

    public void setAnguloObjetivo(Integer anguloObjetivo) {
        this.anguloObjetivo = anguloObjetivo;
    }
}
