package ar.edu.uncuyo.dashboard.auth;

import ar.edu.uncuyo.dashboard.entity.Usuario;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    @Getter
    private final Long id;
    private final String cuenta;
    private final String clave;
    private final String nombre;
    private final String apellido;

    public CustomUserDetails(Usuario usuario) {
        this.id = usuario.getId();
        this.cuenta = usuario.getNombre();
        this.clave = usuario.getClave();
        this.nombre = usuario.getNombre();
        this.apellido = usuario.getApellido();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return List.of(); }

    @Override
    public String getPassword() {
        return this.clave;
    }

    @Override
    public String getUsername() {
        return this.cuenta;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getFullName() {
        return nombre + ' ' + apellido;
    }
}