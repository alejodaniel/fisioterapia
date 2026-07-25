package com.fisio.backend.repository;

import com.fisio.backend.model.SesionTerapia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SesionTerapiaRepository extends JpaRepository<SesionTerapia, Long> {
    List<SesionTerapia> findByPacienteIdOrderByFechaDesc(Long pacienteId);
}
