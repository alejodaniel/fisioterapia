package com.fisio.backend.service;

import com.fisio.backend.model.Ejercicio;
import com.fisio.backend.model.EjercicioAsignado;
import com.fisio.backend.model.Usuario;
import com.fisio.backend.repository.EjercicioAsignadoRepository;
import com.fisio.backend.repository.EjercicioRepository;
import com.fisio.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EjercicioAsignadoService {

    private final EjercicioAsignadoRepository ejercicioAsignadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EjercicioRepository ejercicioRepository;

    @Autowired
    public EjercicioAsignadoService(EjercicioAsignadoRepository ejercicioAsignadoRepository,
                                   UsuarioRepository usuarioRepository,
                                   EjercicioRepository ejercicioRepository) {
        this.ejercicioAsignadoRepository = ejercicioAsignadoRepository;
        this.usuarioRepository = usuarioRepository;
        this.ejercicioRepository = ejercicioRepository;
    }

    public EjercicioAsignado asignarEjercicio(Long pacienteId, Long ejercicioId, String indicaciones) {
        Usuario paciente = usuarioRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Error: Paciente no encontrado con ID: " + pacienteId));

        Ejercicio ejercicio = ejercicioRepository.findById(ejercicioId)
                .orElseThrow(() -> new RuntimeException("Error: Ejercicio no encontrado con ID: " + ejercicioId));

        // Si ya existía asignación previa, actualizar las indicaciones
        Optional<EjercicioAsignado> existente = ejercicioAsignadoRepository.findByPacienteIdAndEjercicioId(pacienteId, ejercicioId);
        if (existente.isPresent()) {
            EjercicioAsignado asignacion = existente.get();
            asignacion.setIndicaciones(indicaciones);
            asignacion.setFechaAsignacion(LocalDateTime.now());
            return ejercicioAsignadoRepository.save(asignacion);
        }

        EjercicioAsignado nuevaAsignacion = new EjercicioAsignado(paciente, ejercicio, indicaciones, LocalDateTime.now());
        return ejercicioAsignadoRepository.save(nuevaAsignacion);
    }

    public List<EjercicioAsignado> listarPorPaciente(Long pacienteId) {
        return ejercicioAsignadoRepository.findByPacienteId(pacienteId);
    }

    public void eliminarAsignacion(Long asignacionId) {
        ejercicioAsignadoRepository.deleteById(asignacionId);
    }
}
