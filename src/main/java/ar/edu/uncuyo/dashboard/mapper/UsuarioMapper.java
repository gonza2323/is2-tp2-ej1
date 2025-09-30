package ar.edu.uncuyo.dashboard.mapper;

import ar.edu.uncuyo.dashboard.dto.UsuarioCreateFormDto;
import ar.edu.uncuyo.dashboard.dto.UsuarioDto;
import ar.edu.uncuyo.dashboard.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    @Mapping(target = "eliminado", ignore = true)
    @Mapping(target = "clave", ignore = true)
    Usuario toEntity(UsuarioCreateFormDto usuarioDto);

    UsuarioDto toDto(Usuario usuario);

    List<UsuarioDto> toDtos(List<Usuario> usuarios);
}
