package ar.edu.uncuyo.dashboard.mapper;

import ar.edu.uncuyo.dashboard.dto.LocalidadDto;
import ar.edu.uncuyo.dashboard.dto.LocalidadResumenDto;
import ar.edu.uncuyo.dashboard.entity.Localidad;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LocalidadMapper {

    @Mapping(target = "departamento", ignore = true)
    @Mapping(target = "eliminado", ignore = true)
    Localidad toEntity(LocalidadDto dto);

    @Mapping(target = "departamento", ignore = true)
    @Mapping(target = "eliminado", ignore = true)
    void updateEntityFromDto(LocalidadDto dto, @MappingTarget Localidad localidad);

    @Mapping(target = "departamentoId", source = "departamento.id")
    LocalidadDto toDto(Localidad localidad);

    List<LocalidadDto> toDtos(List<Localidad> localidades);

    @Mapping(target = "departamentoNombre", source = "departamento.nombre")
    @Mapping(target = "provinciaNombre", source = "departamento.provincia.nombre")
    @Mapping(target = "paisNombre", source = "departamento.provincia.pais.nombre")
    LocalidadResumenDto toSummaryDto(Localidad localidad);

    List<LocalidadResumenDto> toSummaryDtos(List<Localidad> localidades);
}
