package com.fisio.backend.repository;

import com.fisio.backend.model.EjercicioAsignado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EjercicioAsignadoRepository extends JpaRepository<EjercicioAsignado, Long> {
    List<EjercicioAsignado> findByPacienteId(Long pacienteId);
    Optional<EjercicioAsignado> findByPacienteIdAndEjercicioId(Long pacienteId, Long ejercicioId);
}
