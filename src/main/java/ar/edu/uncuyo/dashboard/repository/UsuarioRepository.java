package ar.edu.uncuyo.dashboard.repository;

import ar.edu.uncuyo.dashboard.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByCuentaAndEliminadoFalse(String nombreUsuario);

    Optional<Usuario> findByCuentaAndEliminadoFalse(String nombre);
}
