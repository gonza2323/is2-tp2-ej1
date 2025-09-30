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
        Localidad localidad = localidadService.buscarLocalidad(direccionDto.getLocalidadId());

        Direccion direccion = direccionMapper.toEntity(direccionDto);
        direccion.setId(null);
        direccion.setLocalidad(localidad);
        direccion.setEliminado(false);
        return direccionRepository.save(direccion);
    }

    @Transactional
    public void modificarDireccion(DireccionDto direccionDto) {
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
}
