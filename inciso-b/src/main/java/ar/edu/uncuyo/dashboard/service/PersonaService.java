package ar.edu.uncuyo.dashboard.service;

import ar.edu.uncuyo.dashboard.dto.PersonaDto;
import ar.edu.uncuyo.dashboard.entity.Persona;
import ar.edu.uncuyo.dashboard.error.BusinessException;
import ar.edu.uncuyo.dashboard.mapper.PersonaMapper;
import ar.edu.uncuyo.dashboard.repository.PersonaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonaService {
    private final PersonaMapper personaMapper;
    private final PersonaRepository personaRepository;

    public Persona crearPersona(Persona persona, PersonaDto personaDto) {
        if (personaRepository.existsByCorreoElectronicoAndEliminadoFalse(personaDto.getCorreoElectronico()))
            throw new BusinessException("La dirección de correo electrónico ya está en uso");

        personaMapper.updateEntityFromDto(personaDto, persona);
        return persona;
    }

    public Persona modificarPersona(Persona persona, PersonaDto personaDto) {
        if (personaRepository.existsByCorreoElectronicoAndIdNotAndEliminadoFalse(
                personaDto.getCorreoElectronico(),
                personaDto.getId())) {
            throw new BusinessException("La dirección de correo electrónico ya está en uso");
        };

        personaMapper.updateEntityFromDto(personaDto, persona);
        return persona;
    }

    public void eliminarPersona(Long id) {
        Persona persona = personaRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new BusinessException("Persona no encontrada"));

        persona.setEliminado(true);
        personaRepository.save(persona);
    }
}
