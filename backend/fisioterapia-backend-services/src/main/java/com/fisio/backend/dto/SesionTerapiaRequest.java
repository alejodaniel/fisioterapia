package com.fisio.backend.dto;

import jakarta.validation.constraints.NotNull;

public class SesionTerapiaRequest {
    @NotNull(message = "El id del paciente es requerido")
    private Long pacienteId;

    @NotNull(message = "El id del ejercicio es requerido")
    private Long ejercicioId;

    @NotNull(message = "Las repeticiones exitosas son requeridas")
    private Integer repeticionesExitosas;

    @NotNull(message = "Los errores cometidos son requeridos")
    private Integer erroresCometidos;

    private String observaciones;

    // Getters y Setters
    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }

    public Long getEjercicioId() {
        return ejercicioId;
    }

    public void setEjercicioId(Long ejercicioId) {
        this.ejercicioId = ejercicioId;
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
