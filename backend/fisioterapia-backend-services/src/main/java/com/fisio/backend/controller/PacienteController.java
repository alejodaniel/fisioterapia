package com.fisio.backend.controller;

import com.fisio.backend.dto.RegisterRequest;
import com.fisio.backend.model.Usuario;
import com.fisio.backend.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final UsuarioService usuarioService;
    private final PasswordEncoder encoder;

    @Autowired
    public PacienteController(UsuarioService usuarioService, PasswordEncoder encoder) {
        this.usuarioService = usuarioService;
        this.encoder = encoder;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listarPacientes() {
        List<Usuario> pacientes = usuarioService.listarPacientes();
        return ResponseEntity.ok(pacientes);
    }

    @PostMapping
    public ResponseEntity<?> crearPaciente(@Valid @RequestBody RegisterRequest request) {
        if (usuarioService.existePorCorreo(request.getCorreo())) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error: El correo ya está registrado!");
            return ResponseEntity.badRequest().body(response);
        }

        // Se crea el paciente asegurando que el rol sea siempre "PACIENTE"
        Usuario nuevoPaciente = new Usuario(
                request.getNombre(),
                request.getCorreo(),
                encoder.encode(request.getPassword()),
                "PACIENTE"
        );

        Usuario guardado = usuarioService.guardar(nuevoPaciente);
        return ResponseEntity.ok(guardado);
    }
}
