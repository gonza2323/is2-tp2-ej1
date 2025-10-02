package ar.edu.uncuyo.dashboard.controller;

import ar.edu.uncuyo.dashboard.dto.*;
import ar.edu.uncuyo.dashboard.error.BusinessException;
import ar.edu.uncuyo.dashboard.service.*;
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
public class LocalidadController {
    private final PaisService paisService;
    private final ProvinciaService provinciaService;
    private final DepartamentoService departamentoService;
    private final LocalidadService localidadService;

    private final String vistaLista = "/localidad/localidadLista";
    private final String vistaDetalle = "/localidad/localidadDetalle";
    private final String vistaAlta = "/localidad/localidadAlta";
    private final String vistaEdicion = "/localidad/localidadEdit";
    private final String redirectLista = "/localidades";

    @GetMapping("/localidades")
    public String listarLocalidades(Model model) {
        return prepararVistaLista(model);
    }

    @GetMapping("/localidades/alta")
    public String altaLocalidad(Model model) {
        return prepararVistaFormularioAlta(model, new LocalidadDto());
    }

    @GetMapping("/localidades/{id}/edit")
    public String modificarLocalidad(Model model, @PathVariable Long id) {
        LocalidadDto localidadDto = localidadService.buscarLocalidadDto(id);
        return prepararVistaFormularioEdicion(model, localidadDto);
    }

    @PostMapping("/localidades/alta")
    public String altaLocalidad(Model model, @Valid @ModelAttribute("localidad") LocalidadDto localidadDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return prepararVistaFormularioAlta(model, localidadDto);

        try {
            localidadService.crearLocalidad(localidadDto);
            return "redirect:" + redirectLista;
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessage());
            return prepararVistaFormularioAlta(model, localidadDto);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            model.addAttribute("msgError", "Error de sistema");
            return prepararVistaFormularioAlta(model, localidadDto);
        }
    }

    @PostMapping("/localidades/edit")
    public String modificarLocalidad(Model model, @Valid @ModelAttribute("localidad") LocalidadDto localidadDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return prepararVistaFormularioEdicion(model, localidadDto);

        try {
            localidadService.modificarLocalidad(localidadDto);
            return "redirect:" + redirectLista;
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessage());
            return prepararVistaFormularioEdicion(model, localidadDto);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            model.addAttribute("msgError", "Error de sistema");
            return prepararVistaFormularioEdicion(model, localidadDto);
        }
    }

    @PostMapping("localidades/{id}/baja")
    public String eliminarLocalidad(Model model, @PathVariable Long id) {
        try {
            localidadService.eliminarLocalidad(id);
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
        List<LocalidadResumenDto> localidades = localidadService.listarLocalidadesDtos();
        model.addAttribute("localidades", localidades);
        return vistaLista;
    }

    private void prepararVistaFormulario(Model model, LocalidadDto localidad) {
        List<PaisDto> paises = paisService.listarPaisesDtos();
        model.addAttribute("paises", paises);

        Long paisId = localidad.getPaisId();
        if (paisId != null) {
            List<ProvinciaDto> provincias = provinciaService.buscarProvinciaPorPais(paisId);
            model.addAttribute("provincias", provincias);
        }

        Long provinciaId = localidad.getProvinciaId();
        if (provinciaId != null) {
            List<DepartamentoDto> departamentos = departamentoService.buscarDepartamentosPorProvincia(provinciaId);
            model.addAttribute("departamentos", departamentos);
        }

        model.addAttribute("localidad", localidad);
    }

    private String prepararVistaFormularioAlta(Model model, LocalidadDto localidad) {
        prepararVistaFormulario(model, localidad);
        return vistaAlta;
    }

    private String prepararVistaFormularioEdicion(Model model, LocalidadDto localidad) {
        prepararVistaFormulario(model, localidad);
        return vistaEdicion;
    }

    private String prepararVistaDetalle(Model model, LocalidadDto localidad) {
        prepararVistaFormulario(model, localidad);
        return vistaDetalle;
    }

    @ResponseBody
    @GetMapping("/api/localidades")
    List<LocalidadDto> buscarProvinciasPorPais(@Param("departamentoId") Long departamentoId) {
        return localidadService.buscarLocalidadesPorDepartamento(departamentoId);
    }
}
