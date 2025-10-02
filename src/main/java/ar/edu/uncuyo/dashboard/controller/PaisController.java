package ar.edu.uncuyo.dashboard.controller;

import ar.edu.uncuyo.dashboard.dto.PaisDto;
import ar.edu.uncuyo.dashboard.error.BusinessException;
import ar.edu.uncuyo.dashboard.service.PaisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/paises")
@RequiredArgsConstructor
public class PaisController {
    private final PaisService paisService;

    private final String vistaLista = "/pais/paisLista";
    private final String vistaAlta = "/pais/paisAlta";
    private final String vistaEdicion = "/pais/paisEdit";
    private final String redirectLista = "/paises";

    @GetMapping("")
    public String listarPaises(Model model) {
        return prepararVistaLista(model);
    }

    @GetMapping("/alta")
    public String altaPais(Model model) {
        return prepararVistaFormularioAlta(model, new PaisDto());
    }

    @GetMapping("/{id}/edit")
    public String modificarPais(Model model, @PathVariable Long id) {
        PaisDto paisDto = paisService.buscarPaisDto(id);
        return prepararVistaFormularioEdicion(model, paisDto);
    }

    @PostMapping("/alta")
    public String altaPais(Model model, @Valid @ModelAttribute("pais") PaisDto paisDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return prepararVistaFormularioAlta(model, paisDto);

        try {
            paisService.crearPais(paisDto);
            return "redirect:" + redirectLista;
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessage());
            return prepararVistaFormularioAlta(model, paisDto);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            model.addAttribute("msgError", "Error de sistema");
            return prepararVistaFormularioAlta(model, paisDto);
        }
    }

    @PostMapping("/edit")
    public String modificarPais(Model model, @Valid @ModelAttribute("pais") PaisDto paisDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return prepararVistaFormularioEdicion(model, paisDto);

        try {
            paisService.modificarPais(paisDto);
            return "redirect:" + redirectLista;
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessage());
            return prepararVistaFormularioEdicion(model, paisDto);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            model.addAttribute("msgError", "Error de sistema");
            return prepararVistaFormularioEdicion(model, paisDto);
        }
    }

    @PostMapping("/{id}/baja")
    public String eliminarPais(Model model, @PathVariable Long id) {
        try {
            paisService.eliminarPais(id);
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
        List<PaisDto> paises = paisService.listarPaisesDtos();
        model.addAttribute("paises", paises);
        return vistaLista;
    }

    private String prepararVistaFormularioAlta(Model model, PaisDto pais) {
        model.addAttribute("pais", pais);
        return vistaAlta;
    }

    private String prepararVistaFormularioEdicion(Model model, PaisDto pais) {
        model.addAttribute("pais", pais);
        return vistaEdicion;
    }

}
