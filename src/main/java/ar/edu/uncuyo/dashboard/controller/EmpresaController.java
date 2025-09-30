package ar.edu.uncuyo.dashboard.controller;

import ar.edu.uncuyo.dashboard.dto.*;
import ar.edu.uncuyo.dashboard.error.BusinessException;
import ar.edu.uncuyo.dashboard.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/empresas")
@RequiredArgsConstructor
public class EmpresaController {

    private final EmpresaService empresaService;
    private final PaisService paisService;
    private final ProvinciaService provinciaService;
    private final DepartamentoService departamentoService;
    private final LocalidadService localidadService;

    private final String vistaLista = "/empresa/empresaLista";
    private final String vistaDetalle = "/empresa/empresaDetalle";
    private final String vistaAlta = "/empresa/empresaAlta";
    private final String vistaEdicion = "/empresa/empresaEdit";
    private final String redirectLista = "/empresas";

    @GetMapping("")
    public String listarEmpresas(Model model) {
        return prepararVistaLista(model);
    }

    @GetMapping("/{id}")
    public String detalleEmpresa(Model model, @PathVariable Long id) {
        EmpresaDto empresa = empresaService.buscarEmpresaDto(id);
        return prepararVistaDetalle(model, empresa);
    }

    @GetMapping("/alta")
    public String altaEmpresa(Model model) {
        return prepararVistaFormularioAlta(model, new EmpresaDto());
    }

    @GetMapping("/{id}/edit")
    public String modificarEmpresa(Model model, @PathVariable Long id) {
        EmpresaDto empresaDto = empresaService.buscarEmpresaDto(id);
        return prepararVistaFormularioEdicion(model, empresaDto);
    }

    @PostMapping("/alta")
    public String altaEmpresa(Model model, @Valid @ModelAttribute("empresa") EmpresaDto empresaDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return prepararVistaFormularioAlta(model, empresaDto);

        try {
            empresaService.crearEmpresa(empresaDto);
            return "redirect:" + redirectLista;
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessage());
            return prepararVistaFormularioAlta(model, empresaDto);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            model.addAttribute("msgError", "Error de sistema");
            return prepararVistaFormularioAlta(model, empresaDto);
        }
    }

    @PostMapping("/edit")
    public String modificarEmpresa(Model model, @Valid @ModelAttribute("empresa") EmpresaDto empresaDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return prepararVistaFormularioEdicion(model, empresaDto);

        try {
            empresaService.modificarEmpresa(empresaDto);
            return "redirect:" + redirectLista;
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessage());
            return prepararVistaFormularioEdicion(model, empresaDto);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            model.addAttribute("msgError", "Error de sistema");
            return prepararVistaFormularioEdicion(model, empresaDto);
        }
    }

    @PostMapping("/{id}/baja")
    public String eliminarEmpresa(Model model, @PathVariable Long id) {
        try {
            empresaService.eliminarEmpresa(id);
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
        List<EmpresaDto> empresas = empresaService.listarEmpresasDtos();
        model.addAttribute("empresas", empresas);
        return vistaLista;
    }

    private void prepararVistaFormulario(Model model, EmpresaDto empresaDto) {
        List<PaisDto> paises = paisService.listarPaisesDtos();
        model.addAttribute("paises", paises);

        Long paisId = empresaDto.getDireccion().getPaisId();
        if (paisId != null) {
            List<ProvinciaDto> provincias = provinciaService.buscarProvinciaPorPais(paisId);
            model.addAttribute("provincias", provincias);
        }

        Long provinciaId = empresaDto.getDireccion().getProvinciaId();
        if (provinciaId != null) {
            List<DepartamentoDto> departamentos = departamentoService.buscarDepartamentosPorProvincia(provinciaId);
            model.addAttribute("departamentos", departamentos);
        }

        Long departamentoId = empresaDto.getDireccion().getDepartamentoId();
        if (departamentoId != null) {
            List<LocalidadDto> localidades = localidadService.buscarLocalidadesPorDepartamento(departamentoId);
            model.addAttribute("localidades", localidades);
        }

        model.addAttribute("empresa", empresaDto);
    }

    private String prepararVistaFormularioAlta(Model model, EmpresaDto empresa) {
        prepararVistaFormulario(model, empresa);
        return vistaAlta;
    }

    private String prepararVistaFormularioEdicion(Model model, EmpresaDto empresa) {
        prepararVistaFormulario(model, empresa);
        return vistaEdicion;
    }

    private String prepararVistaDetalle(Model model, EmpresaDto empresa) {
        prepararVistaFormulario(model, empresa);
        return vistaDetalle;
    }
}
