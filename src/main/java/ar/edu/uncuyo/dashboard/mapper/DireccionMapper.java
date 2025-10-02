package ar.edu.uncuyo.dashboard.mapper;


import ar.edu.uncuyo.dashboard.dto.DireccionDto;
import ar.edu.uncuyo.dashboard.entity.Direccion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DireccionMapper {

    //@Mapping(target = "localidad", ignore = true)
    @Mapping(target = "localidad.id", source = "localidadId")
    Direccion toEntity(DireccionDto dto);


    @Mapping(target = "localidad", ignore = true)
    void updateEntityFromDto(DireccionDto dto, @MappingTarget Direccion direccion);

    @Mapping(target = "localidadId", source = "localidad.id")
    @Mapping(target = "departamentoId", source = "localidad.departamento.id")
    @Mapping(target = "provinciaId", source = "localidad.departamento.provincia.id")
    @Mapping(target = "paisId", source = "localidad.departamento.provincia.pais.id")
    DireccionDto toDto(Direccion direccion);

    List<DireccionDto> toDtos(List<Direccion> direccion);
}
