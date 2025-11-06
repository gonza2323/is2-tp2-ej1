package ar.edu.uncuyo.dashboard.mapper;

import ar.edu.uncuyo.dashboard.dto.UsuarioCreateFormDto;
import ar.edu.uncuyo.dashboard.dto.UsuarioDto;
import ar.edu.uncuyo.dashboard.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.context.annotation.Bean;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    @Mapping(target = "eliminado", ignore = true)
    @Mapping(target = "clave", ignore = true)
    Usuario toEntity(UsuarioCreateFormDto usuarioDto);

    @Mapping(target = "eliminado", ignore = true)
    @Mapping(target = "clave", ignore = true)
    Usuario updateEntityFromDto(UsuarioDto usuarioDto, @MappingTarget Usuario usuario);

    UsuarioDto toDto(Usuario usuario);

    List<UsuarioDto> toDtos(List<Usuario> usuarios);
}
