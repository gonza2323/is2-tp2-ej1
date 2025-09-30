package ar.edu.uncuyo.dashboard.repository;

import ar.edu.uncuyo.dashboard.entity.Provincia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProvinciaRepository extends JpaRepository<Provincia, Long> {
    boolean existsByNombreAndPaisIdAndEliminadoFalse(String nombre, Long paisId);
    boolean existsByNombreAndIdNotAndPaisIdAndEliminadoFalse(String nombre, Long id, Long paisId);

    Optional<Provincia> findByIdAndEliminadoFalse(Long id);
    List<Provincia> findAllByEliminadoFalseOrderByNombre();
    List<Provincia> findAllByPaisIdAndEliminadoFalseOrderByNombre(Long paisId);
}
