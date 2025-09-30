package ar.edu.uncuyo.dashboard.service;

import ar.edu.uncuyo.dashboard.dto.UsuarioCreateFormDto;
import ar.edu.uncuyo.dashboard.dto.UsuarioDto;
import ar.edu.uncuyo.dashboard.entity.Usuario;
import ar.edu.uncuyo.dashboard.error.BusinessException;
import ar.edu.uncuyo.dashboard.mapper.UsuarioMapper;
import ar.edu.uncuyo.dashboard.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PersonaService personaService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioDto buscarUsuarioDto(Long id) {
        Usuario usuario = usuarioRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return usuarioMapper.toDto(usuario);
    }

    @Transactional
    public List<UsuarioDto> listarUsuariosDtos() {
        List<Usuario> usuarios = usuarioRepository.findAllByEliminadoFalseOrderByApellidoAscNombreAsc();
        return usuarioMapper.toDtos(usuarios);
    }

    @Transactional
    public void crearUsuario(UsuarioCreateFormDto usuarioDto) {
        validarDatos(usuarioDto);

        Usuario usuario = usuarioMapper.toEntity(usuarioDto);
        personaService.prepararPersona(usuario, usuarioDto.getPersona());

        String clave = passwordEncoder.encode(usuarioDto.getClave());
        usuario.setCuenta(usuarioDto.getPersona().getCorreoElectronico());
        usuario.setClave(clave);

        usuarioRepository.save(usuario);
    }

    @Transactional
    public void eliminarUsuario(Long id){
        personaService.eliminarPersona(id);
    }

    private void validarDatos(UsuarioCreateFormDto usuarioDto) {
        if (!usuarioDto.getClave().equals(usuarioDto.getConfirmacionClave()))
            throw new BusinessException("Las contraseñas no coinciden");
    }
}
