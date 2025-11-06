package ar.edu.uncuyo.dashboard.mapper;

import ar.edu.uncuyo.dashboard.dto.EmpresaDto;
import ar.edu.uncuyo.dashboard.entity.Empresa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {DireccionMapper.class})
public interface EmpresaMapper {

    @Mapping(target = "direccion", ignore = true)
    @Mapping(target = "eliminado", ignore = true)
    Empresa toEntity(EmpresaDto dto);

    @Mapping(target = "direccion", ignore = true)
    @Mapping(target = "eliminado", ignore = true)
    void updateEntityFromDto(EmpresaDto dto, @MappingTarget Empresa empresa);

    @Mapping(target = "nombreLocalidad", source = "direccion.localidad.nombre")
    @Mapping(target = "nombreDepartamento", source = "direccion.localidad.departamento.nombre")
    @Mapping(target = "nombreProvincia", source = "direccion.localidad.departamento.provincia.nombre")
    @Mapping(target = "nombrePais", source = "direccion.localidad.departamento.provincia.pais.nombre")
    EmpresaDto toDto(Empresa empresa);

    List<EmpresaDto> toDtos(List<Empresa> empresa);
}
