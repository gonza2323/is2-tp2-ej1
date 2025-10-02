package ar.edu.uncuyo.dashboard.mapper;

import ar.edu.uncuyo.dashboard.dto.MensajeDTO;
import ar.edu.uncuyo.dashboard.entity.Mensaje;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MensajeMapper {


    @Mapping(target = "proveedor", ignore = true)
    @Mapping(target = "eliminado", ignore = true)
    Mensaje toEntity(MensajeDTO dto);


    @Mapping(target = "proveedorId", source = "proveedor.id")
    MensajeDTO toDto(Mensaje mensaje);


    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "nombre")
    @Mapping(target = "correoElectronico")
    @Mapping(target = "asunto")
    @Mapping(target = "cuerpo")
    @Mapping(target = "fechaProgramada")
    void updateEntityFromDto(MensajeDTO dto, @MappingTarget Mensaje mensaje);
}
