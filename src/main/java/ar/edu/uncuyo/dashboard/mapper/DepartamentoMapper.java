package ar.edu.uncuyo.dashboard.mapper;

import ar.edu.uncuyo.dashboard.dto.DepartamentoDto;
import ar.edu.uncuyo.dashboard.dto.DepartamentoResumenDto;
import ar.edu.uncuyo.dashboard.entity.Departamento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DepartamentoMapper {

    @Mapping(target = "provincia", ignore = true)
    @Mapping(target = "eliminado", ignore = true)
    Departamento toEntity(DepartamentoDto dto);

    @Mapping(target = "provincia", ignore = true)
    @Mapping(target = "eliminado", ignore = true)
    void updateEntityFromDto(DepartamentoDto dto, @MappingTarget Departamento departamento);

    @Mapping(target = "provinciaId", source = "provincia.id")
    @Mapping(target = "paisId", source = "provincia.pais.id")
    DepartamentoDto toDto(Departamento departamento);

    List<DepartamentoDto> toDtos(List<Departamento> departamentos);

    @Mapping(target = "provinciaId", source = "provincia.id")
    @Mapping(target = "paisId", source = "provincia.pais.id")
    @Mapping(target = "provinciaNombre", source = "provincia.nombre")
    @Mapping(target = "paisNombre", source = "provincia.pais.nombre")
    DepartamentoResumenDto toSummaryDto(Departamento departamento);

    List<DepartamentoResumenDto> toSummaryDtos(List<Departamento> departamentos);
}