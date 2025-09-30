package ar.edu.uncuyo.dashboard.repository;

import ar.edu.uncuyo.dashboard.entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    Optional<Proveedor> findByIdAndEliminadoFalse(Long id);
    List<Proveedor> findAllByEliminadoFalseOrderByApellidoAscNombreAsc();
}
