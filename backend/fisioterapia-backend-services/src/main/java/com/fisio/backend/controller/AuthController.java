package com.fisio.backend.controller;

import com.fisio.backend.dto.LoginRequest;
import com.fisio.backend.dto.RegisterRequest;
import com.fisio.backend.dto.JwtResponse;
import com.fisio.backend.model.Usuario;
import com.fisio.backend.security.jwt.JwtUtils;
import com.fisio.backend.security.services.UserDetailsImpl;
import com.fisio.backend.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UsuarioService usuarioService,
                          PasswordEncoder encoder, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.usuarioService = usuarioService;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getCorreo(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Extraer rol quitando el prefijo "ROLE_"
        String rol = userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getNombre(),
                userDetails.getCorreo(),
                rol));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest signUpRequest) {
        if (usuarioService.existePorCorreo(signUpRequest.getCorreo())) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error: El correo ya está registrado!");
            return ResponseEntity.badRequest().body(response);
        }

        // Crear nuevo usuario
        Usuario user = new Usuario(signUpRequest.getNombre(),
                signUpRequest.getCorreo(),
                encoder.encode(signUpRequest.getPassword()),
                signUpRequest.getRol().toUpperCase());

        usuarioService.guardar(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Usuario registrado exitosamente!");
        return ResponseEntity.ok(response);
    }
}
