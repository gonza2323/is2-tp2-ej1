package ar.edu.uncuyo.dashboard.repository;

import ar.edu.uncuyo.dashboard.entity.Localidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LocalidadRepository extends JpaRepository<Localidad, Long> {
    boolean existsByNombreAndDepartamentoIdAndEliminadoFalse(String nombre, Long deparamentoId);
    boolean existsByNombreAndIdNotAndDepartamentoIdAndEliminadoFalse(String nombre, Long id, Long departamentoId);

    List<Localidad> findAllByEliminadoFalseOrderByNombre();
    Optional<Localidad> findByIdAndEliminadoFalse(Long id);
    List<Localidad> findAllByDepartamentoIdAndEliminadoFalseOrderByNombre(Long departamentoId);

    @Query("SELECT l FROM Localidad l " +
            "WHERE l.nombre = :nombreLocalidad " +
            "AND l.departamento.nombre = :nombreDepartamento " +
            "AND l.departamento.provincia.nombre = :nombreProvincia " +
            "AND l.departamento.provincia.pais.nombre = :nombrePais")
    List<Localidad> findByNombreCompleto(
            @Param("nombreLocalidad") String nombreLocalidad,
            @Param("nombreDepartamento") String nombreDepartamento,
            @Param("nombreProvincia") String nombreProvincia,
            @Param("nombrePais") String nombrePais
    );}
