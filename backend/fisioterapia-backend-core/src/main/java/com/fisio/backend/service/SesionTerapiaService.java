package com.fisio.backend.service;

import com.fisio.backend.model.SesionTerapia;
import com.fisio.backend.repository.SesionTerapiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SesionTerapiaService {

    private final SesionTerapiaRepository sesionTerapiaRepository;

    @Autowired
    public SesionTerapiaService(SesionTerapiaRepository sesionTerapiaRepository) {
        this.sesionTerapiaRepository = sesionTerapiaRepository;
    }

    public List<SesionTerapia> obtenerHistorialPaciente(Long pacienteId) {
        return sesionTerapiaRepository.findByPacienteIdOrderByFechaDesc(pacienteId);
    }

    public SesionTerapia guardarSesion(SesionTerapia sesion) {
        return sesionTerapiaRepository.save(sesion);
    }
}
