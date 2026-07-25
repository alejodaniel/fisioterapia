package com.fisio.backend;

import com.fisio.backend.model.Ejercicio;
import com.fisio.backend.model.Usuario;
import com.fisio.backend.service.EjercicioService;
import com.fisio.backend.service.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication(scanBasePackages = "com.fisio.backend")
@EnableJpaRepositories(basePackages = "com.fisio.backend.repository")
@EntityScan(basePackages = "com.fisio.backend.model")
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    CommandLineRunner initData(UsuarioService usuarioService, EjercicioService ejercicioService, PasswordEncoder encoder) {
        return args -> {
            // Inicializar usuarios por defecto si no existen
            if (!usuarioService.existePorCorreo("paciente@fisioterapia.com")) {
                Usuario paciente = new Usuario(
                        "Juan Perez",
                        "paciente@fisioterapia.com",
                        encoder.encode("password123"),
                        "PACIENTE"
                );
                usuarioService.guardar(paciente);
            }

            if (!usuarioService.existePorCorreo("medico@fisioterapia.com")) {
                Usuario medico = new Usuario(
                        "Dr. Carlos Gomez",
                        "medico@fisioterapia.com",
                        encoder.encode("password123"),
                        "MEDICO"
                );
                usuarioService.guardar(medico);
            }

            // Inicializar ejercicios por defecto si no existen (región cervical y mano/muñeca)
            if (ejercicioService.obtenerTodos().isEmpty()) {
                ejercicioService.guardar(new Ejercicio(
                        "Inclinación Lateral de Cuello",
                        "Estiramiento cervical lateral inclinando la cabeza hacia el hombro izquierdo o derecho. Ángulo de flexión lateral objetivo: 35 grados.",
                        35
                ));
                ejercicioService.guardar(new Ejercicio(
                        "Flexión de Muñeca",
                        "Movimiento vertical de la mano hacia arriba y abajo apoyando el antebrazo en la mesa. Ángulo objetivo de flexión dorsal: 60 grados.",
                        60
                ));
                ejercicioService.guardar(new Ejercicio(
                        "Apertura de Dedos",
                        "Extensión completa de la palma abriendo los dedos al máximo y cerrando el puño. Medición de amplitud de apertura objetivo: 90% respecto al rango máximo.",
                        90
                ));
            }
        };
    }
}
