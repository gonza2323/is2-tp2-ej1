package ar.edu.uncuyo.dashboard.service;

import ar.edu.uncuyo.dashboard.dto.DireccionDto;
import ar.edu.uncuyo.dashboard.dto.ProveedorDto;
import ar.edu.uncuyo.dashboard.entity.*;
import ar.edu.uncuyo.dashboard.mapper.DireccionMapper;
import ar.edu.uncuyo.dashboard.mapper.ProveedorMapper;
import ar.edu.uncuyo.dashboard.repository.*;
import ar.edu.uncuyo.dashboard.txt.TxtImporter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProveedorService {
    private final ProveedorRepository proveedorRepository;
    private final ProveedorMapper proveedorMapper;
    private final PersonaService personaService;
    private final DireccionService direccionService;
    private final TxtImporter txtImporter;
    private final PaisRepository paisRepository;
    private final ProvinciaRepository provinciaRepository;
    private final DepartamentoRepository departamentoRepository;
    private final LocalidadRepository localidadRepository;
    private final DireccionRepository direccionRepository;
    private final DireccionMapper direccionMapper;

    @Transactional
    public Proveedor buscarProveedor(Long id) {
        return proveedorRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
    }

    @Transactional
    public ProveedorDto buscarProveedorDto(Long id) {
        Proveedor proveedor = buscarProveedor(id);
        return proveedorMapper.toDto(proveedor);
    }

    @Transactional
    public List<ProveedorDto> listarProveedoresDtos() {
        List<Proveedor> proveedores = proveedorRepository.findAllByEliminadoFalseOrderByApellidoAscNombreAsc();
        return proveedorMapper.toDtos(proveedores);
    }

    @Transactional
    public void crearProveedor(ProveedorDto proveedorDto) {
        Direccion direccion = direccionService.crearDireccion(proveedorDto.getDireccion());

        Proveedor proveedor = proveedorMapper.toEntity(proveedorDto);
        personaService.crearPersona(proveedor, proveedorDto.getPersona());
        proveedor.setDireccion(direccion);

        proveedorRepository.save(proveedor);
    }

    @Transactional
    public void modificarProveedor(ProveedorDto proveedorDto) {
        Proveedor proveedor = buscarProveedor(proveedorDto.getId());
        proveedorMapper.updateEntityFromDto(proveedorDto, proveedor);

        proveedorDto.getPersona().setId(proveedor.getId());
        personaService.modificarPersona(proveedor, proveedorDto.getPersona());

        DireccionDto direccionDto = proveedorDto.getDireccion();
        direccionDto.setId(proveedor.getDireccion().getId());
        direccionService.modificarDireccion(direccionDto);

        proveedorRepository.save(proveedor);
    }

    @Transactional
    public void eliminarProveedor(Long id) {
        Proveedor proveedor = buscarProveedor(id);
        proveedor.getDireccion().setEliminado(true);
        personaService.eliminarPersona(id);
    }

    @Transactional
    public List<ProveedorDto> importarDesdeTxt() {
        List<ProveedorDto> dtos = txtImporter.leerArchivo();
        List<Proveedor> proveedores = new ArrayList<>();

        for (ProveedorDto dto : dtos) {

            DireccionDto direccion = dto.getDireccion();

            Pais pais = paisRepository.findByNombreAndEliminadoFalse(direccion.getNombrePais())
                    .orElseGet(() -> paisRepository.save(
                            Pais.builder()
                                    .nombre(direccion.getNombrePais())
                                    .eliminado(false)
                                    .build()
                    ));

            Provincia provincia = provinciaRepository.findByNombreAndEliminadoFalse(direccion.getNombreProvincia())
                    .orElseGet(() -> provinciaRepository.save(
                            Provincia.builder()
                                    .nombre(direccion.getNombreProvincia())
                                    .pais(pais)
                                    .eliminado(false)
                                    .build()
                    ));

            Departamento depto = departamentoRepository.findByNombreAndEliminadoFalse(direccion.getNombreDepartamento())
                    .orElseGet(() -> departamentoRepository.save(
                            Departamento.builder()
                                    .nombre(direccion.getNombreDepartamento())
                                    .provincia(provincia)
                                    .eliminado(false)
                                    .build()
                    ));

            List<Localidad> resultados = localidadRepository.findByNombreCompleto(
                    direccion.getNombreLocalidad(),
                    direccion.getNombreDepartamento(),
                    direccion.getNombreProvincia(),
                    direccion.getNombrePais()
            );

            Localidad localidad;
            if (resultados.isEmpty()) {
                localidad = localidadRepository.save(
                        Localidad.builder()
                                .nombre(direccion.getNombreLocalidad())
                                .departamento(depto)
                                .eliminado(false)
                                .build()
                );
            } else {
                localidad = resultados.get(0);
            }

            direccion.setLocalidadId(localidad.getId());

            System.out.println("Localidad persistida con id=" + localidad.getId());

            Direccion direccionEntity = Direccion.builder()
                    .calle(direccion.getCalle())
                    .numeracion(direccion.getNumeracion())
                    .localidad(localidad)
                    .eliminado(false)
                    .build();

            direccionEntity = direccionRepository.save(direccionEntity);

            Proveedor proveedor = proveedorMapper.toEntity(dto);
            proveedor.setDireccion(direccionEntity);
            proveedores.add(proveedorRepository.save(proveedor));
        }
        return proveedores.stream()
                .map(proveedorMapper::toDto)
                .toList();
    }

}
