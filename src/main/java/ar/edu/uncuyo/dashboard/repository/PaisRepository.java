package ar.edu.uncuyo.dashboard.repository;

import ar.edu.uncuyo.dashboard.entity.Pais;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaisRepository extends JpaRepository<Pais, Long> {
    boolean existsByNombreAndEliminadoFalse(String nombre);
    boolean existsByNombreAndIdNotAndEliminadoFalse(String nombre, Long id);

    List<Pais> findAllByEliminadoFalseOrderByNombre();
    Optional<Pais> findByIdAndEliminadoFalse(Long id);

    Optional<Pais> findByNombreAndEliminadoFalse(String nombrePais);
}
