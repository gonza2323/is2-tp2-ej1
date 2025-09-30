package ar.edu.uncuyo.dashboard.controller;

import ar.edu.uncuyo.dashboard.dto.UsuarioCreateFormDto;
import ar.edu.uncuyo.dashboard.dto.UsuarioDto;
import ar.edu.uncuyo.dashboard.error.BusinessException;
import ar.edu.uncuyo.dashboard.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    private final String vistaLista = "/usuario/usuarioLista";
    private final String vistaDetalle = "/usuario/usuarioDetalle";
    private final String vistaAlta = "/usuario/usuarioAlta";
    private final String vistaEdicion = "/usuario/usuarioEdit";
    private final String redirectLista = "/usuarios";

    @GetMapping("")
    public String listarUsuarios(Model model) {
        return prepararVistaLista(model);
    }

    @GetMapping("/{id}")
    public String detalleUsuario(Model model, @PathVariable Long id) {
        UsuarioDto usuario = usuarioService.buscarUsuarioDto(id);
        model.addAttribute("usuario", usuario);
        return vistaDetalle;
    }

    @GetMapping("/alta")
    public String altaUsuario(Model model) {
        return prepararVistaFormularioAlta(model, new UsuarioCreateFormDto());
    }

    @GetMapping("/{id}/edit")
    public String modificarUsuario(Model model, @PathVariable Long id) {
        UsuarioDto usuario = usuarioService.buscarUsuarioDto(id);
        return prepararVistaFormularioEdicion(model, usuario);
    }

    @PostMapping("/alta")
    public String altaUsuario(Model model, @Valid @ModelAttribute("usuario") UsuarioCreateFormDto usuarioDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return prepararVistaFormularioAlta(model, usuarioDto);

        try {
            usuarioService.crearUsuario(usuarioDto);
            return "redirect:" + redirectLista;
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessage());
            return prepararVistaFormularioAlta(model, usuarioDto);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            model.addAttribute("msgError", "Error de sistema");
            return prepararVistaFormularioAlta(model, usuarioDto);
        }
    }

    @PostMapping("/edit")
    public String modificarUsuario(Model model, @Valid @ModelAttribute("usuario") UsuarioDto usuarioDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return prepararVistaFormularioEdicion(model, usuarioDto);

        try {
            usuarioService.modificarUsuario(usuarioDto);
            return "redirect:" + redirectLista;
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessage());
            return prepararVistaFormularioEdicion(model, usuarioDto);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            model.addAttribute("msgError", "Error de sistema");
            return prepararVistaFormularioEdicion(model, usuarioDto);
        }
    }

    @PostMapping("/{id}/baja")
    public String eliminarUsuario(Model model, @PathVariable Long id) {
        try {
            usuarioService.eliminarUsuario(id);
            return "redirect:" + redirectLista;
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessage());
            return prepararVistaLista(model);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            model.addAttribute("msgError", "Error de sistema");
            return prepararVistaLista(model);
        }
    }

    private String prepararVistaLista(Model model) {
        List<UsuarioDto> usuarios = usuarioService.listarUsuariosDtos();
        model.addAttribute("usuarios", usuarios);
        return vistaLista;
    }

    private String prepararVistaFormularioAlta(Model model, UsuarioCreateFormDto usuario) {
        model.addAttribute("usuario", usuario);
        return vistaAlta;
    }

    private String prepararVistaFormularioEdicion(Model model, UsuarioDto usuario) {
        model.addAttribute("usuario", usuario);
        return vistaEdicion;
    }
}
