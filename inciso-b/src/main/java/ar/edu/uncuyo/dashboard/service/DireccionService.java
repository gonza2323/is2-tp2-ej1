package ar.edu.uncuyo.dashboard.service;

import ar.edu.uncuyo.dashboard.dto.DireccionDto;
import ar.edu.uncuyo.dashboard.entity.Direccion;
import ar.edu.uncuyo.dashboard.entity.Localidad;
import ar.edu.uncuyo.dashboard.error.BusinessException;
import ar.edu.uncuyo.dashboard.mapper.DireccionMapper;
import ar.edu.uncuyo.dashboard.repository.DireccionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DireccionService {

    private final DireccionRepository direccionRepository;
    private final DireccionMapper direccionMapper;
    private final LocalidadService localidadService;

    public Direccion buscarDireccion(Long id) {
        return direccionRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new BusinessException("La dirección no existe"));
    }

    @Transactional
    public DireccionDto buscarDireccionDto(Long id) {
        Direccion direccion = buscarDireccion(id);
        return direccionMapper.toDto(direccion);
    }

    @Transactional
    public Direccion crearDireccion(DireccionDto direccionDto) {
        validar(direccionDto);

        Localidad localidad = localidadService.buscarLocalidad(direccionDto.getLocalidadId());

        Direccion direccion = direccionMapper.toEntity(direccionDto);
        direccion.setId(null);
        direccion.setLocalidad(localidad);
        direccion.setEliminado(false);
        return direccionRepository.save(direccion);
    }

    @Transactional
    public void modificarDireccion(DireccionDto direccionDto) {
        validar(direccionDto);

        Direccion direccion = buscarDireccion(direccionDto.getId());

        Localidad localidad = localidadService.buscarLocalidad(direccionDto.getLocalidadId());

        direccionMapper.updateEntityFromDto(direccionDto, direccion);
        direccion.setLocalidad(localidad);
        direccionRepository.save(direccion);
    }

    @Transactional
    public void eliminarDireccion(Long id) {
        Direccion direccion = buscarDireccion(id);
        direccion.setEliminado(true);
        direccionRepository.save(direccion);
    }

    private void validar(DireccionDto direccion) {
        Double lat = direccion.getLatitud();
        Double lon = direccion.getLongitud();

        if (lat == null && lon == null) {
            return;
        }

        if (lat == null || lon == null) {
            throw new BusinessException("Ingrese latitud y longitud, o ninguna de ellas");
        }

        if (lat < -90.0 || lat > 90.0) {
            throw new BusinessException("La latitud debe ser entre -90° y 90°");
        }

        if (lon < -180.0 || lon > 180.0) {
            throw new BusinessException("La longitud debe ser entre -180° y 180°.");
        }
    }
}
