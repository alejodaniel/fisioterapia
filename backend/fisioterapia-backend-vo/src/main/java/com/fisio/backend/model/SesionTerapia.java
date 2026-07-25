package com.fisio.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "sesiones_terapia")
public class SesionTerapia {

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

    @NotNull
    private LocalDateTime fecha;

    @NotNull
    @Column(name = "repeticiones_exitosas")
    private Integer repeticionesExitosas;

    @NotNull
    @Column(name = "errores_cometidos")
    private Integer erroresCometidos;

    @Column(length = 500)
    private String observaciones;

    // Constructores
    public SesionTerapia() {}

    public SesionTerapia(Usuario paciente, Ejercicio ejercicio, LocalDateTime fecha, Integer repeticionesExitosas, Integer erroresCometidos, String observaciones) {
        this.paciente = paciente;
        this.ejercicio = ejercicio;
        this.fecha = fecha;
        this.repeticionesExitosas = repeticionesExitosas;
        this.erroresCometidos = erroresCometidos;
        this.observaciones = observaciones;
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

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Integer getRepeticionesExitosas() {
        return repeticionesExitosas;
    }

    public void setRepeticionesExitosas(Integer repeticionesExitosas) {
        this.repeticionesExitosas = repeticionesExitosas;
    }

    public Integer getErroresCometidos() {
        return erroresCometidos;
    }

    public void setErroresCometidos(Integer erroresCometidos) {
        this.erroresCometidos = erroresCometidos;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
