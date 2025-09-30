package ar.edu.uncuyo.dashboard.repository;

import ar.edu.uncuyo.dashboard.entity.Persona;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonaRepository extends JpaRepository<Persona, Long> {
    boolean existsByCorreoElectronicoAndEliminadoFalse(String correoElectronico);
    boolean existsByCorreoElectronicoAndIdNotAndEliminadoFalse(String correoElectronico, Long id);

    Optional<Persona> findByIdAndEliminadoFalse(Long id);
}
