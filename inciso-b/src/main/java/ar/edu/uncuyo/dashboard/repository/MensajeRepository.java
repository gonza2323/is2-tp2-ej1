package ar.edu.uncuyo.dashboard.repository;

import ar.edu.uncuyo.dashboard.entity.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MensajeRepository extends JpaRepository<Mensaje, Long> {

    List<Mensaje> findAllByEliminadoFalse();

    Optional<Mensaje> findByIdAndEliminadoFalse(Long id);
}
