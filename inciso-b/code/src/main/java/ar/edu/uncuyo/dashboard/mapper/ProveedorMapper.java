package ar.edu.uncuyo.dashboard.mapper;

import ar.edu.uncuyo.dashboard.dto.ProveedorDto;
import ar.edu.uncuyo.dashboard.entity.Proveedor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {DireccionMapper.class})
public interface ProveedorMapper {
    @Mapping(target = "direccion", ignore = true)
    @Mapping(target = "eliminado", ignore = true)
    @Mapping(target = "nombre", source = "persona.nombre")
    @Mapping(target = "apellido", source = "persona.apellido")
    @Mapping(target = "telefono", source = "persona.telefono")
    @Mapping(target = "correoElectronico", source = "persona.correoElectronico")
    Proveedor toEntity(ProveedorDto dto);

    @Mapping(target = "direccion", ignore = true)
    @Mapping(target = "eliminado", ignore = true)
    void updateEntityFromDto(ProveedorDto dto, @MappingTarget Proveedor proveedor);

    @Mapping(target = "persona.id", source = "id")
    @Mapping(target = "persona.nombre", source = "nombre")
    @Mapping(target = "persona.apellido", source = "apellido")
    @Mapping(target = "persona.correoElectronico", source = "correoElectronico")
    @Mapping(target = "persona.telefono", source = "telefono")
    @Mapping(target = "direccion.localidadId", source = "direccion.localidad.id")
    @Mapping(target = "direccion.departamentoId", source = "direccion.localidad.departamento.id")
    @Mapping(target = "nombreProvincia", source = "direccion.localidad.departamento.provincia.nombre")
    @Mapping(target = "nombrePais", source = "direccion.localidad.departamento.provincia.pais.nombre")
    ProveedorDto toDto(Proveedor proveedor);

    List<ProveedorDto> toDtos(List<Proveedor> proveedores);
}
