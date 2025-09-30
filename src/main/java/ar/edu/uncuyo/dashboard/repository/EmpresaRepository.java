package ar.edu.uncuyo.dashboard.repository;

import ar.edu.uncuyo.dashboard.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    List<Empresa> findAllByEliminadoFalseOrderByRazonSocial();
    List<Empresa> findAllByRazonSocial(String razonSocial);

    Optional<Empresa> findByIdAndEliminadoFalse(Long id);
    Optional<Empresa> findByRazonSocialAndEliminadoFalse(String razonSocial);
}
