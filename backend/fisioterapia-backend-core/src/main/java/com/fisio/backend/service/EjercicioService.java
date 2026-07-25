package com.fisio.backend.service;

import com.fisio.backend.model.Ejercicio;
import com.fisio.backend.repository.EjercicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class EjercicioService {

    private final EjercicioRepository ejercicioRepository;

    @Autowired
    public EjercicioService(EjercicioRepository ejercicioRepository) {
        this.ejercicioRepository = ejercicioRepository;
    }

    public List<Ejercicio> obtenerTodos() {
        return ejercicioRepository.findAll();
    }

    public Optional<Ejercicio> buscarPorId(Long id) {
        return ejercicioRepository.findById(id);
    }

    public Ejercicio guardar(Ejercicio ejercicio) {
        return ejercicioRepository.save(ejercicio);
    }
}
