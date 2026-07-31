package com.fisio.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "ejercicios_asignados")
public class EjercicioAsignado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "paciente_id", nullable = false)
    @NotNull
    private Usuario paciente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ejercicio_id", nullable = false)
    @NotNull
    private Ejercicio ejercicio;

    @Column(length = 500)
    private String indicaciones;

    @NotNull
    @Column(name = "fecha_asignacion")
    private LocalDateTime fechaAsignacion;

    // Constructores
    public EjercicioAsignado() {}

    public EjercicioAsignado(Usuario paciente, Ejercicio ejercicio, String indicaciones, LocalDateTime fechaAsignacion) {
        this.paciente = paciente;
        this.ejercicio = ejercicio;
        this.indicaciones = indicaciones;
        this.fechaAsignacion = fechaAsignacion;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getPaciente() {
        return paciente;
    }

    public void setPaciente(Usuario paciente) {
        this.paciente = paciente;
    }

    public Ejercicio getEjercicio() {
        return ejercicio;
    }

    public void setEjercicio(Ejercicio ejercicio) {
        this.ejercicio = ejercicio;
    }

    public String getIndicaciones() {
        return indicaciones;
    }

    public void setIndicaciones(String indicaciones) {
        this.indicaciones = indicaciones;
    }

    public LocalDateTime getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(LocalDateTime fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }
}
