package ar.edu.uncuyo.dashboard.init;

import ar.edu.uncuyo.dashboard.entity.Usuario;
import ar.edu.uncuyo.dashboard.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitialization implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        crearDatosIniciales();
    }

    @Transactional
    protected void crearDatosIniciales() {
        if (usuarioRepository.existsByCuentaAndEliminadoFalse("pepeargento@gmail.com")) {
            System.out.println("Datos iniciales ya creados. Salteando creación de datos iniciales. Para forzar su creación, borrar la base de datos");
            return;
        }

        // Nos damos permisos para poder crear los datos iniciales
        var auth = new UsernamePasswordAuthenticationToken("system", null);
        SecurityContextHolder.getContext().setAuthentication(auth);

        System.out.println("Creando datos iniciales...");

        // Creación de datos iniciales
        crearUsuarios();
//        crearPaises();
//        cargarUbicaciones();
//        crearEmpresa();
//        crearSucursales();
//        crearSocios();
//        crearEmpleados();
//        crearValoresCuota();
//        crearCuotas();

        // Resetear los permisos
        SecurityContextHolder.clearContext();

        System.out.println("Datos iniciales creados.");
    }

    @Transactional
    protected void crearUsuarios() {
        Usuario usuario = Usuario.builder()
                .nombre("Pepe")
                .apellido("Argento")
                .correoElectronico("pepeargento@gmail.com")
                .cuenta("pepeargento@gmail.com")
                .telefono("11 6473 9202")
                .clave(passwordEncoder.encode("1234"))
                .build();

        usuarioRepository.save(usuario);
    }
}