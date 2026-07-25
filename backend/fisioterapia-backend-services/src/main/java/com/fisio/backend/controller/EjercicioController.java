package com.fisio.backend.controller;

import com.fisio.backend.model.Ejercicio;
import com.fisio.backend.service.EjercicioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/ejercicios")
public class EjercicioController {

    private final EjercicioService ejercicioService;

    @Autowired
    public EjercicioController(EjercicioService ejercicioService) {
        this.ejercicioService = ejercicioService;
    }

    @GetMapping
    public ResponseEntity<List<Ejercicio>> obtenerTodos() {
        return ResponseEntity.ok(ejercicioService.obtenerTodos());
    }

    @PostMapping
    @PreAuthorize("hasRole('MEDICO')")
    public ResponseEntity<Ejercicio> crearEjercicio(@Valid @RequestBody Ejercicio ejercicio) {
        Ejercicio nuevo = ejercicioService.guardar(ejercicio);
        return ResponseEntity.ok(nuevo);
    }
}
