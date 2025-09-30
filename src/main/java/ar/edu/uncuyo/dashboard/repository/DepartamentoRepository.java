package ar.edu.uncuyo.dashboard.repository;

import ar.edu.uncuyo.dashboard.entity.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {
    boolean existsByNombreAndProvinciaIdAndEliminadoFalse(String nombre, Long provinciaId);
    boolean existsByNombreAndIdNotAndProvinciaIdAndEliminadoFalse(String nombre, Long id, Long provinciaId);

    Optional<Departamento> findByIdAndEliminadoFalse(Long id);
    List<Departamento> findAllByProvinciaIdAndEliminadoFalseOrderByNombre(Long provinciaId);
}
