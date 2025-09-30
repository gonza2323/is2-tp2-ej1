package ar.edu.uncuyo.dashboard.repository;

import ar.edu.uncuyo.dashboard.entity.Localidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LocalidadRepository extends JpaRepository<Localidad, Long> {
    boolean existsByNombreAndDepartamentoIdAndEliminadoFalse(String nombre, Long deparamentoId);
    boolean existsByNombreAndIdNotAndDepartamentoIdAndEliminadoFalse(String nombre, Long id, Long departamentoId);

    Optional<Localidad> findByIdAndEliminadoFalse(Long id);
    List<Localidad> findAllByDepartamentoIdAndEliminadoFalseOrderByNombre(Long departamentoId);
}
