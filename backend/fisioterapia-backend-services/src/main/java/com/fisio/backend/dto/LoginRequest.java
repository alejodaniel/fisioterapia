package com.fisio.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank(message = "El correo es requerido")
    @Email(message = "Debe proporcionar un correo válido")
    private String correo;

    @NotBlank(message = "La contraseña es requerida")
    private String password;

    // Getters y Setters
    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
