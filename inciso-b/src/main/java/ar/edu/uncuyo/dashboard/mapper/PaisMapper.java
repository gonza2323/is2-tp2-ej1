package ar.edu.uncuyo.dashboard.mapper;

import ar.edu.uncuyo.dashboard.dto.PaisDto;
import ar.edu.uncuyo.dashboard.entity.Pais;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaisMapper {
    @Mapping(target = "eliminado", ignore = true)
    Pais toEntity(PaisDto dto);

    @Mapping(target = "eliminado", ignore = true)
    void updateEntityFromDto(PaisDto dto, @MappingTarget Pais pais);

    PaisDto toDto(Pais pais);
    List<PaisDto> toDtos(List<Pais> paises);
}
