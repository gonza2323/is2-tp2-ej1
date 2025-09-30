package ar.edu.uncuyo.dashboard.service;

import ar.edu.uncuyo.dashboard.dto.LocalidadDto;
import ar.edu.uncuyo.dashboard.entity.Departamento;
import ar.edu.uncuyo.dashboard.entity.Localidad;
import ar.edu.uncuyo.dashboard.error.BusinessException;
import ar.edu.uncuyo.dashboard.mapper.LocalidadMapper;
import ar.edu.uncuyo.dashboard.repository.LocalidadRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocalidadService {
    private final LocalidadRepository localidadRepository;
    private final DepartamentoService departamentoService;
    private final LocalidadMapper localidadMapper;

    @Transactional
    public void crearLocalidad(LocalidadDto localidadDto) {
        if (localidadRepository.existsByNombreAndDepartamentoIdAndEliminadoFalse(localidadDto.getNombre(), localidadDto.getDepartamentoId()))
            throw new BusinessException("Ya existe una localidad con ese nombre en ese departamento");

        Departamento departamento = departamentoService.buscarDepartamento(localidadDto.getDepartamentoId());

        Localidad localidad = localidadMapper.toEntity(localidadDto);
        localidad.setId(null);
        localidad.setDepartamento(departamento);
        localidad.setEliminado(false);
        localidadRepository.save(localidad);
    }

    public Localidad buscarLocalidad(Long id) {
        return localidadRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new BusinessException("La localidad no existe"));
    }

    public List<LocalidadDto> buscarLocalidadesPorDepartamento(Long departamentoId) {
        List<Localidad> localidades = localidadRepository.findAllByDepartamentoIdAndEliminadoFalseOrderByNombre(departamentoId);
        return localidadMapper.toDtos(localidades);
    }
}
