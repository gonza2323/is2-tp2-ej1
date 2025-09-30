package ar.edu.uncuyo.dashboard.mapper;

import ar.edu.uncuyo.dashboard.dto.ProvinciaDto;
import ar.edu.uncuyo.dashboard.dto.ProvinciaResumenDto;
import ar.edu.uncuyo.dashboard.entity.Provincia;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProvinciaMapper {

    @Mapping(target = "pais", ignore = true)
    @Mapping(target = "eliminado", ignore = true)
    Provincia toEntity(ProvinciaDto dto);

    @Mapping(target = "pais", ignore = true)
    @Mapping(target = "eliminado", ignore = true)
    void updateEntityFromDto(ProvinciaDto dto, @MappingTarget Provincia provincia);

    @Mapping(target = "paisId", source = "pais.id")
    ProvinciaDto toDto(Provincia provincia);

    List<ProvinciaDto> toDtos(List<Provincia> provincias);

    @Mapping(target = "paisNombre", source = "pais.nombre")
    ProvinciaResumenDto toSummaryDto(Provincia province);

    List<ProvinciaResumenDto> toSummaryDtos(List<Provincia> provincias);
}
