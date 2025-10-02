package ar.edu.uncuyo.dashboard.controller;

import ar.edu.uncuyo.dashboard.dto.*;
import ar.edu.uncuyo.dashboard.error.BusinessException;
import ar.edu.uncuyo.dashboard.service.DepartamentoService;
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
public class DepartamentoController {
    private final PaisService paisService;
    private final ProvinciaService provinciaService;
    private final DepartamentoService departamentoService;

    private final String vistaLista = "/departamento/departamentoLista";
    private final String vistaDetalle = "/departamento/departamentoDetalle";
    private final String vistaAlta = "/departamento/departamentoAlta";
    private final String vistaEdicion = "/departamento/departamentoEdit";
    private final String redirectLista = "/departamentos";

    @GetMapping("/departamentos")
    public String listarDepartamentos(Model model) {
        return prepararVistaLista(model);
    }

    @GetMapping("/departamentos/alta")
    public String altaDepartamento(Model model) {
        return prepararVistaFormularioAlta(model, new DepartamentoDto());
    }

    @GetMapping("/departamentos/{id}/edit")
    public String modificarDepartamento(Model model, @PathVariable Long id) {
        DepartamentoDto departamentoDto = departamentoService.buscarDepartamentoDto(id);
        return prepararVistaFormularioEdicion(model, departamentoDto);
    }

    @PostMapping("/departamentos/alta")
    public String altaDepartamento(Model model, @Valid @ModelAttribute("departamento") DepartamentoDto departamentoDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return prepararVistaFormularioAlta(model, departamentoDto);

        try {
            departamentoService.crearDepartamento(departamentoDto);
            return "redirect:" + redirectLista;
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessage());
            return prepararVistaFormularioAlta(model, departamentoDto);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            model.addAttribute("msgError", "Error de sistema");
            return prepararVistaFormularioAlta(model, departamentoDto);
        }
    }

    @PostMapping("/departamentos/edit")
    public String modificarDepartamento(Model model, @Valid @ModelAttribute("departamento") DepartamentoDto departamentoDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return prepararVistaFormularioEdicion(model, departamentoDto);

        try {
            departamentoService.modificarDepartamento(departamentoDto);
            return "redirect:" + redirectLista;
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessage());
            return prepararVistaFormularioEdicion(model, departamentoDto);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            model.addAttribute("msgError", "Error de sistema");
            return prepararVistaFormularioEdicion(model, departamentoDto);
        }
    }

    @PostMapping("/departamentos/{id}/baja")
    public String eliminarDepartamento(Model model, @PathVariable Long id) {
        try {
            departamentoService.eliminarDepartamento(id);
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
        List<DepartamentoResumenDto> departamentos = departamentoService.listarDepartamentosDtos();
        model.addAttribute("departamentos", departamentos);
        return vistaLista;
    }

    private void prepararVistaFormulario(Model model, DepartamentoDto departamento) {
        List<PaisDto> paises = paisService.listarPaisesDtos();
        model.addAttribute("paises", paises);

        Long paisId = departamento.getPaisId();
        if (paisId != null) {
            List<ProvinciaDto> provincias = provinciaService.buscarProvinciaPorPais(paisId);
            model.addAttribute("provincias", provincias);
        }

        model.addAttribute("departamento", departamento);
    }

    private String prepararVistaFormularioAlta(Model model, DepartamentoDto departamento) {
        prepararVistaFormulario(model, departamento);
        return vistaAlta;
    }

    private String prepararVistaFormularioEdicion(Model model, DepartamentoDto departamento) {
        prepararVistaFormulario(model, departamento);
        return vistaEdicion;
    }

    private String prepararVistaDetalle(Model model, DepartamentoDto departamento) {
        prepararVistaFormulario(model, departamento);
        return vistaDetalle;
    }

    @ResponseBody
    @GetMapping("/api/departamentos")
    List<DepartamentoDto> buscarProvinciasPorPais(@Param("provinciaId") Long provinciaId) {
        return departamentoService.buscarDepartamentosPorProvincia(provinciaId);
    }
}
