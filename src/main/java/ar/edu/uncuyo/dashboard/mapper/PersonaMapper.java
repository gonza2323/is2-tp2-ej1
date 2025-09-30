package ar.edu.uncuyo.dashboard.mapper;

import ar.edu.uncuyo.dashboard.dto.PersonaDto;
import ar.edu.uncuyo.dashboard.entity.Persona;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PersonaMapper {
    @Mapping(target = "eliminado",  ignore = true)
    void updateEntityFromDto(PersonaDto dto, @MappingTarget Persona persona);
}
