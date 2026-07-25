package com.fisio.backend.controller;

import com.fisio.backend.dto.SesionTerapiaRequest;
import com.fisio.backend.model.Ejercicio;
import com.fisio.backend.model.SesionTerapia;
import com.fisio.backend.model.Usuario;
import com.fisio.backend.service.EjercicioService;
import com.fisio.backend.service.SesionTerapiaService;
import com.fisio.backend.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/sesiones")
public class SesionController {

    private final SesionTerapiaService sesionTerapiaService;
    private final UsuarioService usuarioService;
    private final EjercicioService ejercicioService;

    @Autowired
    public SesionController(SesionTerapiaService sesionTerapiaService, UsuarioService usuarioService,
                            EjercicioService ejercicioService) {
        this.sesionTerapiaService = sesionTerapiaService;
        this.usuarioService = usuarioService;
        this.ejercicioService = ejercicioService;
    }

    @GetMapping("/{id_paciente}")
    public ResponseEntity<?> obtenerHistorialPaciente(@PathVariable("id_paciente") Long idPaciente) {
        Optional<Usuario> pacienteOpt = usuarioService.buscarPorId(idPaciente);
        if (pacienteOpt.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Paciente no encontrado");
            return ResponseEntity.badRequest().body(response);
        }

        List<SesionTerapia> historial = sesionTerapiaService.obtenerHistorialPaciente(idPaciente);
        return ResponseEntity.ok(historial);
    }

    @PostMapping
    public ResponseEntity<?> guardarSesion(@Valid @RequestBody SesionTerapiaRequest request) {
        Optional<Usuario> pacienteOpt = usuarioService.buscarPorId(request.getPacienteId());
        if (pacienteOpt.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Paciente no encontrado con id: " + request.getPacienteId());
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Ejercicio> ejercicioOpt = ejercicioService.buscarPorId(request.getEjercicioId());
        if (ejercicioOpt.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Ejercicio no encontrado con id: " + request.getEjercicioId());
            return ResponseEntity.badRequest().body(response);
        }

        // Crear la entidad SesionTerapia
        SesionTerapia sesion = new SesionTerapia(
                pacienteOpt.get(),
                ejercicioOpt.get(),
                LocalDateTime.now(),
                request.getRepeticionesExitosas(),
                request.getErroresCometidos(),
                request.getObservaciones()
        );

        SesionTerapia guardada = sesionTerapiaService.guardarSesion(sesion);
        return ResponseEntity.ok(guardada);
    }
}
