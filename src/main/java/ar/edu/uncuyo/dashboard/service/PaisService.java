package ar.edu.uncuyo.dashboard.service;

import ar.edu.uncuyo.dashboard.dto.PaisDto;
import ar.edu.uncuyo.dashboard.entity.Pais;
import ar.edu.uncuyo.dashboard.error.BusinessException;
import ar.edu.uncuyo.dashboard.mapper.PaisMapper;
import ar.edu.uncuyo.dashboard.repository.PaisRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaisService {
    private final PaisRepository paisRepository;
    private final PaisMapper paisMapper;

    @Transactional
    public Pais buscarPais(Long id) {
        return paisRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new BusinessException("El país no existe"));
    }

    @Transactional
    public PaisDto buscarPaisDto(Long id) {
        Pais pais = buscarPais(id);
        return paisMapper.toDto(pais);
    }

    @Transactional
    public List<PaisDto> listarPaisesDtos() {
        List<Pais> paises = paisRepository.findAllByEliminadoFalseOrderByNombre();
        return paisMapper.toDtos(paises);
    }

    @Transactional
    public void crearPais(PaisDto paisDto) {
        if (paisRepository.existsByNombreAndEliminadoFalse(paisDto.getNombre()))
            throw new BusinessException("Ya existe un país con ese nombre");

        Pais pais = paisMapper.toEntity(paisDto);
        pais.setId(null);
        paisRepository.save(pais);
    }

    @Transactional
    public void modificarPais(PaisDto paisDto) {
        Pais pais = buscarPais(paisDto.getId());

        if (paisRepository.existsByNombreAndIdNotAndEliminadoFalse(paisDto.getNombre(), paisDto.getId()))
            throw new BusinessException("Ya existe un país con ese nombre");

        paisMapper.updateEntityFromDto(paisDto, pais);
        paisRepository.save(pais);
    }

    @Transactional
    public void eliminarPais(Long id) {
        Pais pais = buscarPais(id);
        pais.setEliminado(true);
        paisRepository.save(pais);
    }
}
