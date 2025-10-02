package ar.edu.uncuyo.dashboard.controller;

import ar.edu.uncuyo.dashboard.dto.*;
import ar.edu.uncuyo.dashboard.error.BusinessException;
import ar.edu.uncuyo.dashboard.service.PaisService;
import ar.edu.uncuyo.dashboard.service.ProvinciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProvinciaController {
    private final PaisService paisService;
    private final ProvinciaService provinciaService;

    private final String vistaLista = "/provincia/provinciaLista";
    private final String vistaDetalle = "/provincia/provinciaDetalle";
    private final String vistaAlta = "/provincia/provinciaAlta";
    private final String vistaEdicion = "/provincia/provinciaEdit";
    private final String redirectLista = "/provincias";

    @GetMapping("/provincias")
    public String listarProvincias(Model model) {
        return prepararVistaLista(model);
    }

    @GetMapping("/provincias/alta")
    public String altaProvincia(Model model) {
        return prepararVistaFormularioAlta(model, new ProvinciaDto());
    }

    @GetMapping("/provincias/{id}/edit")
    public String modificarProvincia(Model model, @PathVariable Long id) {
        ProvinciaDto provinciaDto = provinciaService.buscarProvinciaDto(id);
        return prepararVistaFormularioEdicion(model, provinciaDto);
    }

    @PostMapping("/provincias/alta")
    public String altaProvincia(Model model, @Valid @ModelAttribute("provincia") ProvinciaDto provinciaDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return prepararVistaFormularioAlta(model, provinciaDto);

        try {
            provinciaService.crearProvincia(provinciaDto);
            return "redirect:" + redirectLista;
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessage());
            return prepararVistaFormularioAlta(model, provinciaDto);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            model.addAttribute("msgError", "Error de sistema");
            return prepararVistaFormularioAlta(model, provinciaDto);
        }
    }

    @PostMapping("/provincias/edit")
    public String modificarProvincia(Model model, @Valid @ModelAttribute("provincia") ProvinciaDto provinciaDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return prepararVistaFormularioEdicion(model, provinciaDto);

        try {
            provinciaService.modificarProvincia(provinciaDto);
            return "redirect:" + redirectLista;
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessage());
            return prepararVistaFormularioEdicion(model, provinciaDto);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            model.addAttribute("msgError", "Error de sistema");
            return prepararVistaFormularioEdicion(model, provinciaDto);
        }
    }

    @PostMapping("/provincias/{id}/baja")
    public String eliminarProvincia(Model model, @PathVariable Long id) {
        try {
            provinciaService.eliminarProvincia(id);
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
        List<ProvinciaResumenDto> provincias = provinciaService.listarProvinciasDtos();
        model.addAttribute("provincias", provincias);
        return vistaLista;
    }

    private void prepararVistaFormulario(Model model, ProvinciaDto provincia) {
        List<PaisDto> paises = paisService.listarPaisesDtos();
        model.addAttribute("paises", paises);
        model.addAttribute("provincia", provincia);
    }

    private String prepararVistaFormularioAlta(Model model, ProvinciaDto provincia) {
        prepararVistaFormulario(model, provincia);
        return vistaAlta;
    }

    private String prepararVistaFormularioEdicion(Model model, ProvinciaDto provincia) {
        prepararVistaFormulario(model, provincia);
        return vistaEdicion;
    }

    private String prepararVistaDetalle(Model model, ProvinciaDto provincia) {
        prepararVistaFormulario(model, provincia);
        return vistaDetalle;
    }

    @ResponseBody
    @GetMapping("/api/provincias")
    List<ProvinciaDto> buscarProvinciasPorPais(@Param("paisId") Long paisId) {
        return provinciaService.buscarProvinciaPorPais(paisId);
    }
}
