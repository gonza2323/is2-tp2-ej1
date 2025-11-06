package ar.edu.uncuyo.dashboard.service;

import ar.edu.uncuyo.dashboard.dto.DireccionDto;
import ar.edu.uncuyo.dashboard.dto.EmpresaDto;
import ar.edu.uncuyo.dashboard.dto.LocalidadDto;
import ar.edu.uncuyo.dashboard.dto.LocalidadResumenDto;
import ar.edu.uncuyo.dashboard.entity.Departamento;
import ar.edu.uncuyo.dashboard.entity.Empresa;
import ar.edu.uncuyo.dashboard.entity.Localidad;
import ar.edu.uncuyo.dashboard.error.BusinessException;
import ar.edu.uncuyo.dashboard.mapper.LocalidadMapper;
import ar.edu.uncuyo.dashboard.repository.LocalidadRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocalidadService {
    private final LocalidadRepository localidadRepository;
    private final DepartamentoService departamentoService;
    private final LocalidadMapper localidadMapper;

    @Transactional
    public Localidad buscarLocalidad(Long id) {
        return localidadRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new BusinessException("La localidad no existe"));
    }

    @Transactional
    public LocalidadDto buscarLocalidadDto(Long id) {
        Localidad localidad = buscarLocalidad(id);
        return localidadMapper.toDto(localidad);
    }

    @Transactional
    public List<LocalidadResumenDto> listarLocalidadesDtos() {
        List<Localidad> localidades = localidadRepository.findAllByEliminadoFalseOrderByNombre();
        return localidadMapper.toSummaryDtos(localidades);
    }

    @Transactional
    public void crearLocalidad(LocalidadDto localidadDto) {
        if (localidadRepository.existsByNombreAndDepartamentoIdAndEliminadoFalse(localidadDto.getNombre(), localidadDto.getDepartamentoId()))
            throw new BusinessException("Ya existe una localidad con ese nombre en ese departamento");

        Departamento departamento = departamentoService.buscarDepartamento(localidadDto.getDepartamentoId());

        Localidad localidad = localidadMapper.toEntity(localidadDto);
        localidad.setDepartamento(departamento);
        localidadRepository.save(localidad);
    }

    @Transactional
    public void modificarLocalidad(LocalidadDto localidadDto){
        Localidad localidad = buscarLocalidad(localidadDto.getId());

        if (localidadRepository.existsByNombreAndIdNotAndDepartamentoIdAndEliminadoFalse(
                localidadDto.getNombre(),
                localidadDto.getId(),
                localidadDto.getDepartamentoId()))
            throw new BusinessException("Ya existe una localidad con ese nombre en ese departamento");

        Departamento departamento = departamentoService.buscarDepartamento(localidadDto.getDepartamentoId());

        localidadMapper.updateEntityFromDto(localidadDto, localidad);
        localidad.setDepartamento(departamento);
        localidadRepository.save(localidad);
    }

    @Transactional
    public void eliminarLocalidad(Long id) {
        Localidad localidad = buscarLocalidad(id);
        localidad.setEliminado(true);
        localidadRepository.save(localidad);
    }

    @Transactional
    public List<LocalidadDto> buscarLocalidadesPorDepartamento(Long departamentoId) {
        List<Localidad> localidades = localidadRepository.findAllByDepartamentoIdAndEliminadoFalseOrderByNombre(departamentoId);
        return localidadMapper.toDtos(localidades);
    }
}
