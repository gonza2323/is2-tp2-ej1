package ar.edu.uncuyo.dashboard.service;

import ar.edu.uncuyo.dashboard.dto.DireccionDto;
import ar.edu.uncuyo.dashboard.dto.EmpresaDto;
import ar.edu.uncuyo.dashboard.entity.Direccion;
import ar.edu.uncuyo.dashboard.entity.Empresa;
import ar.edu.uncuyo.dashboard.error.BusinessException;
import ar.edu.uncuyo.dashboard.mapper.EmpresaMapper;
import ar.edu.uncuyo.dashboard.repository.EmpresaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpresaService {
    private final EmpresaRepository empresaRepository;
    private final DireccionService direccionService;
    private final EmpresaMapper empresaMapper;

    @Transactional
    public Empresa buscarEmpresa(Long id) {
        return empresaRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new BusinessException("Empresa no encontrada"));
    }

    @Transactional
    public EmpresaDto buscarEmpresaDto(Long id) {
        Empresa empresa = empresaRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new BusinessException("Empresa no encontrada"));
        return empresaMapper.toDto(empresa);
    }

    @Transactional
    public List<EmpresaDto> listarEmpresasDtos() {
        List<Empresa> empresa = empresaRepository.findAllByEliminadoFalseOrderByRazonSocial();
        return empresaMapper.toDtos(empresa);
    }

    @Transactional
    public Empresa crearEmpresa(EmpresaDto empresaDto) {

        Direccion direccion = direccionService.crearDireccion(empresaDto.getDireccion());

        Empresa empresa = empresaMapper.toEntity(empresaDto);
        empresa.setDireccion(direccion);

        return empresaRepository.save(empresa);
    }

    @Transactional
    public void modificarEmpresa(EmpresaDto empresaDto){
        Empresa empresa = empresaRepository.findByIdAndEliminadoFalse(empresaDto.getId())
                .orElseThrow(() -> new BusinessException("Empresa no encontrada"));

        empresaMapper.updateEntityFromDto(empresaDto, empresa);

        DireccionDto direccionDto = empresaDto.getDireccion();
        direccionDto.setId(empresa.getDireccion().getId());
        direccionService.modificarDireccion(direccionDto);

        empresaRepository.save(empresa);
    }

    @Transactional
    public void eliminarEmpresa(Long id){
        Empresa empresa = empresaRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new BusinessException("Empresa no encontrada"));

        empresa.setEliminado(true);
        empresa.getDireccion().setEliminado(true);
        empresaRepository.save(empresa);
    }
}
