package ar.edu.uncuyo.dashboard.service;

import ar.edu.uncuyo.dashboard.dto.DireccionDto;
import ar.edu.uncuyo.dashboard.dto.ProveedorDto;
import ar.edu.uncuyo.dashboard.entity.Direccion;
import ar.edu.uncuyo.dashboard.entity.Proveedor;
import ar.edu.uncuyo.dashboard.mapper.ProveedorMapper;
import ar.edu.uncuyo.dashboard.repository.ProveedorRepository;
import ar.edu.uncuyo.dashboard.txt.TxtImporter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProveedorService {
    private final ProveedorRepository proveedorRepository;
    private final ProveedorMapper proveedorMapper;
    private final PersonaService personaService;
    private final DireccionService direccionService;
    private final TxtImporter txtImporter;



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

    public List<ProveedorDto> importarDesdeTxt() {
        List<ProveedorDto> dtos = txtImporter.leerArchivo();

        // mapear DTO -> entidad
        List<Proveedor> entidades = dtos.stream()
                .map(proveedorMapper::toEntity)
                .toList();

        proveedorRepository.saveAll(entidades);

        return dtos;
    }
}
