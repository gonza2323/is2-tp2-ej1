package ar.edu.uncuyo.dashboard.service;

import ar.edu.uncuyo.dashboard.dto.DepartamentoDto;
import ar.edu.uncuyo.dashboard.entity.Departamento;
import ar.edu.uncuyo.dashboard.entity.Provincia;
import ar.edu.uncuyo.dashboard.error.BusinessException;
import ar.edu.uncuyo.dashboard.mapper.DepartamentoMapper;
import ar.edu.uncuyo.dashboard.repository.DepartamentoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartamentoService {
    private final DepartamentoRepository departamentoRepository;
    private final ProvinciaService provinciaService;
    private final DepartamentoMapper departamentoMapper;

    @Transactional
    public void crearDepartamento(DepartamentoDto departamentoDto) {
        if (departamentoRepository.existsByNombreAndProvinciaIdAndEliminadoFalse(departamentoDto.getNombre(), departamentoDto.getProvinciaId()))
            throw new BusinessException("Ya existe un departamento con ese nombre en esa provincia");

        Provincia provincia = provinciaService.buscarProvincia(departamentoDto.getProvinciaId());

        Departamento departamento = departamentoMapper.toEntity(departamentoDto);
        departamento.setId(null);
        departamento.setProvincia(provincia);
        departamento.setEliminado(false);
        departamentoRepository.save(departamento);
    }

    public Departamento buscarDepartamento(Long id) {
        return departamentoRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new BusinessException("NoExiste.departamento"));
    }

    public List<DepartamentoDto> buscarDepartamentosPorProvincia(Long provinciaId) {
        List<Departamento> departamentos = departamentoRepository.findAllByProvinciaIdAndEliminadoFalseOrderByNombre(provinciaId);
        return departamentoMapper.toDtos(departamentos);
    }
}

