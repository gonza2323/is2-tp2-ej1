package ar.edu.uncuyo.dashboard.service;

import ar.edu.uncuyo.dashboard.dto.ProveedorDto;
import ar.edu.uncuyo.dashboard.entity.Direccion;
import ar.edu.uncuyo.dashboard.entity.Proveedor;
import ar.edu.uncuyo.dashboard.mapper.ProveedorMapper;
import ar.edu.uncuyo.dashboard.repository.ProveedorRepository;
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
        personaService.prepararPersona(proveedor, proveedorDto.getPersona());
        proveedor.setDireccion(direccion);

        proveedorRepository.save(proveedor);
    }

    @Transactional
    public void eliminarProveedor(Long id) {
        Proveedor proveedor = buscarProveedor(id);
        proveedor.getDireccion().setEliminado(true);
        personaService.eliminarPersona(id);
    }
}
