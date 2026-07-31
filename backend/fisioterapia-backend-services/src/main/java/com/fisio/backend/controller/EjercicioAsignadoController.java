package com.fisio.backend.controller;

import com.fisio.backend.dto.EjercicioAsignadoRequest;
import com.fisio.backend.model.EjercicioAsignado;
import com.fisio.backend.service.EjercicioAsignadoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/asignaciones")
public class EjercicioAsignadoController {

    private final EjercicioAsignadoService ejercicioAsignadoService;

    @Autowired
    public EjercicioAsignadoController(EjercicioAsignadoService ejercicioAsignadoService) {
        this.ejercicioAsignadoService = ejercicioAsignadoService;
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<EjercicioAsignado>> obtenerAsignacionesPorPaciente(@PathVariable Long pacienteId) {
        List<EjercicioAsignado> asignaciones = ejercicioAsignadoService.listarPorPaciente(pacienteId);
        return ResponseEntity.ok(asignaciones);
    }

    @PostMapping
    public ResponseEntity<?> asignarEjercicio(@Valid @RequestBody EjercicioAsignadoRequest request) {
        try {
            EjercicioAsignado asignado = ejercicioAsignadoService.asignarEjercicio(
                    request.getPacienteId(),
                    request.getEjercicioId(),
                    request.getIndicaciones()
            );
            return ResponseEntity.ok(asignado);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarAsignacion(@PathVariable Long id) {
        try {
            ejercicioAsignadoService.eliminarAsignacion(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Asignación eliminada exitosamente!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
