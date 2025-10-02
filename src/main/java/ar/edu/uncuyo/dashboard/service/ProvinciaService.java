package ar.edu.uncuyo.dashboard.service;

import ar.edu.uncuyo.dashboard.dto.ProvinciaDto;
import ar.edu.uncuyo.dashboard.dto.ProvinciaResumenDto;
import ar.edu.uncuyo.dashboard.entity.Pais;
import ar.edu.uncuyo.dashboard.entity.Provincia;
import ar.edu.uncuyo.dashboard.error.BusinessException;
import ar.edu.uncuyo.dashboard.mapper.ProvinciaMapper;
import ar.edu.uncuyo.dashboard.repository.ProvinciaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProvinciaService {
    private final ProvinciaRepository provinciaRepository;
    private final PaisService paisService;
    private final ProvinciaMapper provinciaMapper;

    @Transactional
    public Provincia buscarProvincia(Long id) {
        return provinciaRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new BusinessException("NoExiste.provincia"));
    }

    @Transactional
    public ProvinciaDto buscarProvinciaDto(Long id) {
        Provincia provincia = buscarProvincia(id);
        return provinciaMapper.toDto(provincia);
    }

    @Transactional
    public List<ProvinciaResumenDto> listarProvinciasDtos() {
        List<Provincia> provincias = provinciaRepository.findAllByEliminadoFalseOrderByNombre();
        return provinciaMapper.toSummaryDtos(provincias);
    }

    @Transactional
    public void crearProvincia(ProvinciaDto provinciaDto) {
        if (provinciaRepository.existsByNombreAndPaisIdAndEliminadoFalse(provinciaDto.getNombre(), provinciaDto.getPaisId()))
            throw new BusinessException("Ya existe una provincia con ese nombre en ese país");

        Pais pais = paisService.buscarPais(provinciaDto.getPaisId());

        Provincia provincia = provinciaMapper.toEntity(provinciaDto);
        provincia.setPais(pais);
        provinciaRepository.save(provincia);
    }

    @Transactional
    public void modificarProvincia(ProvinciaDto provinciaDto) {
        Provincia provincia = buscarProvincia(provinciaDto.getId());

        if (provinciaRepository.existsByNombreAndIdNotAndPaisIdAndEliminadoFalse(provinciaDto.getNombre(), provinciaDto.getId(), provinciaDto.getPaisId()))
            throw new BusinessException("Ya existe una provincia con ese nombre en ese país");

        Pais pais = paisService.buscarPais(provinciaDto.getPaisId());

        provinciaMapper.updateEntityFromDto(provinciaDto, provincia);
        provincia.setPais(pais);
        provinciaRepository.save(provincia);
    }

    @Transactional
    public void eliminarProvincia(Long id) {
        Provincia provincia = buscarProvincia(id);
        provincia.setEliminado(true);
        provinciaRepository.save(provincia);
    }

    public List<ProvinciaDto> buscarProvinciaPorPais(Long paisId) {
        List<Provincia> provincias = provinciaRepository.findAllByPaisIdAndEliminadoFalseOrderByNombre(paisId);
        return provinciaMapper.toDtos(provincias);
    }
}
