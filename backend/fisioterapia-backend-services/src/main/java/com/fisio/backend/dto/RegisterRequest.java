package com.fisio.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    @NotBlank(message = "El nombre es requerido")
    @Size(max = 100)
    private String nombre;

    @NotBlank(message = "El correo es requerido")
    @Email(message = "Debe proporcionar un correo válido")
    @Size(max = 100)
    private String correo;

    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 6, max = 120, message = "La contraseña debe tener entre 6 y 120 caracteres")
    private String password;

    @NotBlank(message = "El rol es requerido")
    @Size(max = 20)
    private String rol; // "PACIENTE" o "MEDICO"

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

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

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
