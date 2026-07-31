package com.fisio.backend.dto;

import jakarta.validation.constraints.NotNull;

public class EjercicioAsignadoRequest {

    @NotNull
    private Long pacienteId;

    @NotNull
    private Long ejercicioId;

    private String indicaciones;

    public EjercicioAsignadoRequest() {}

    public EjercicioAsignadoRequest(Long pacienteId, Long ejercicioId, String indicaciones) {
        this.pacienteId = pacienteId;
        this.ejercicioId = ejercicioId;
        this.indicaciones = indicaciones;
    }

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

    public String getIndicaciones() {
        return indicaciones;
    }

    public void setIndicaciones(String indicaciones) {
        this.indicaciones = indicaciones;
    }
}
