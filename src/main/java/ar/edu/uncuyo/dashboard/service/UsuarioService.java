package ar.edu.uncuyo.dashboard.service;

import ar.edu.uncuyo.dashboard.auth.CustomUserDetails;
import ar.edu.uncuyo.dashboard.dto.CambiarClaveFormDto;
import ar.edu.uncuyo.dashboard.dto.UsuarioCreateFormDto;
import ar.edu.uncuyo.dashboard.dto.UsuarioDto;
import ar.edu.uncuyo.dashboard.entity.Usuario;
import ar.edu.uncuyo.dashboard.error.BusinessException;
import ar.edu.uncuyo.dashboard.mapper.UsuarioMapper;
import ar.edu.uncuyo.dashboard.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PersonaService personaService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario buscarUsuario(Long id) {
        return usuarioRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Transactional
    public UsuarioDto buscarUsuarioDto(Long id) {
        Usuario usuario = buscarUsuario(id);
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
        personaService.crearPersona(usuario, usuarioDto.getPersona());

        String clave = passwordEncoder.encode(usuarioDto.getClave());
        usuario.setCuenta(usuarioDto.getPersona().getCorreoElectronico());
        usuario.setClave(clave);

        usuarioRepository.save(usuario);
    }

    @Transactional
    public void modificarUsuario(UsuarioDto usuarioDto) {
        validarDatos(usuarioDto);

        Usuario usuario = buscarUsuario(usuarioDto.getId());
        personaService.modificarPersona(usuario, usuarioDto);

        usuario.setCuenta(usuarioDto.getCorreoElectronico());
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void eliminarUsuario(Long id){
        personaService.eliminarPersona(id);
    }

    @Transactional
    public void cambiarClave(CambiarClaveFormDto form){
        Usuario usuario = buscarUsuarioActual();

        String claveActual = passwordEncoder.encode(form.getClaveActual());
        if (!passwordEncoder.matches(form.getClaveActual(), usuario.getClave()))
            throw new BusinessException("La contraseña actual es incorrecta");

        if (!form.getNuevaClave().equals(form.getConfirmacionClave()))
            throw new BusinessException("Las contraseñas no coinciden");

        if (form.getClaveActual().equals(form.getNuevaClave()))
            throw new BusinessException("La nueva contraseña debe ser distinta a la actual");

        String nuevaClave = passwordEncoder.encode(form.getNuevaClave());
        usuario.setClave(nuevaClave);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario buscarUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return buscarUsuario(userDetails.getId());
    }

    private void validarDatos(UsuarioCreateFormDto usuarioDto) {
        if (!usuarioDto.getClave().equals(usuarioDto.getConfirmacionClave()))
            throw new BusinessException("Las contraseñas no coinciden");

        if (usuarioRepository.existsByCuentaAndEliminadoFalse(usuarioDto.getPersona().getCorreoElectronico())) {
            throw new BusinessException("La dirección de correo electrónico ya está en uso");
        }
    }

    private void validarDatos(UsuarioDto usuarioDto) {
        if (usuarioRepository.existsByCuentaAndIdNotAndEliminadoFalse(
                usuarioDto.getCorreoElectronico(),
                usuarioDto.getId())) {
            throw new BusinessException("La dirección de correo electrónico ya está en uso");
        }
    }
}
