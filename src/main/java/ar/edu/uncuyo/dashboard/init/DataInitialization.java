package ar.edu.uncuyo.dashboard.init;

import ar.edu.uncuyo.dashboard.dto.PaisDto;
import ar.edu.uncuyo.dashboard.entity.*;
import ar.edu.uncuyo.dashboard.init.geo.*;
import ar.edu.uncuyo.dashboard.repository.*;
import ar.edu.uncuyo.dashboard.service.PaisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DataInitialization implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final PaisService paisService;
    private final PaisRepository paisRepository;
    private final ObjectMapper objectMapper;
    private final ProvinciaRepository provinciaRepository;
    private final DepartamentoRepository departamentoRepository;
    private final LocalidadRepository localidadRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        crearDatosIniciales();
    }

    @Transactional
    protected void crearDatosIniciales() throws Exception {
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
        crearPaises();
        cargarUbicaciones();
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
    protected void crearPaises() {
        paisService.crearPais(new PaisDto(null, "Argentina"));
        paisService.crearPais(new PaisDto(null, "España"));
    }

    @Transactional
    protected void cargarUbicaciones() throws Exception {
        Pais argentina = paisRepository.findById(1L)
                .orElseThrow(() -> new IllegalStateException("Argentina no encontrada"));

        Map<Long, Provincia> provinciaMap = loadProvincias(argentina);
        Map<Long, Departamento> departamentoMap = loadDepartamentos(provinciaMap);
        loadLocalidades(departamentoMap);
    }

    @Transactional
    protected Map<Long, Provincia> loadProvincias(Pais argentina) throws IOException {
        InputStream is = getClass().getResourceAsStream("/data/provincias.json");
        ProvinciasWrapper wrapper = objectMapper.readValue(is, ProvinciasWrapper.class);

        Map<Long, Provincia> provinciaMap = new HashMap<>(wrapper.getProvincias().size());

        for (ProvinciaDTO dto : wrapper.getProvincias()) {
            Long id = dto.getIdAsLong();
            if (!provinciaRepository.existsById(id)) {
                Provincia provincia = new Provincia();
                provincia.setNombre(dto.getNombre());
                provincia.setPais(argentina);
                provinciaMap.put(dto.getIdAsLong(), provincia);
            }
        }
        provinciaRepository.saveAll(provinciaMap.values());

        return provinciaMap;
    }

    @Transactional
    protected Map<Long, Departamento> loadDepartamentos(Map<Long, Provincia> provinciaMap) throws IOException {
        InputStream is = getClass().getResourceAsStream("/data/departamentos.json");
        DepartamentosWrapper wrapper = objectMapper.readValue(is, DepartamentosWrapper.class);

        Map<Long, Departamento> departamentoMap = new HashMap<>(wrapper.getDepartamentos().size());

        for (DepartamentoDTO dto : wrapper.getDepartamentos()) {
            Long id = dto.getIdAsLong();
            if (!departamentoRepository.existsById(id)) {
                Long provinciaId = dto.getProvincia().getIdAsLong();
                Provincia provincia = provinciaMap.get(provinciaId);
                if (provincia == null) {
                    throw new IllegalStateException("Provincia no encontrada, id: " + provinciaId);
                }

                Departamento departamento = new Departamento();
                departamento.setNombre(dto.getNombre());
                departamento.setProvincia(provincia);
                departamentoMap.put(dto.getIdAsLong(), departamento);
            }
        }

        departamentoRepository.saveAll(departamentoMap.values());
        return departamentoMap;
    }

    @Transactional
    protected void loadLocalidades(Map<Long, Departamento> departamentoMap) throws IOException {
        InputStream is = getClass().getResourceAsStream("/data/localidades.json");
        LocalidadesWrapper wrapper = objectMapper.readValue(is, LocalidadesWrapper.class);

        List<Localidad> localidadesToSave = new ArrayList<>(wrapper.getLocalidades().size());
        int postalCodeCounter = 1;

        for (LocalidadDTO dto : wrapper.getLocalidades()) {
            Long id = dto.getIdAsLong();
            if (!localidadRepository.existsById(id)) {
                Long departamentoId = dto.getDepartamento().getIdAsLong();
                Departamento departamento = departamentoMap.get(departamentoId);
                if (departamento == null)
                    new IllegalStateException("Departamento no encontrado, id: " + departamentoId);

                Localidad localidad = new Localidad();
                localidad.setNombre(dto.getNombre());
                localidad.setDepartamento(departamento);

                Provincia provincia = departamento.getProvincia();
                String provinceInitial = provincia.getNombre().substring(0, 1).toUpperCase();
                String numberPart = String.format("%04d", postalCodeCounter++);
                localidad.setCodigoPostal(provinceInitial + numberPart);

                localidadesToSave.add(localidad);
            }
        }

        localidadRepository.saveAll(localidadesToSave);
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