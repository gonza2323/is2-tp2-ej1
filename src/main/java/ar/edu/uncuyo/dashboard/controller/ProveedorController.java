package ar.edu.uncuyo.dashboard.controller;

import ar.edu.uncuyo.dashboard.dto.*;
import ar.edu.uncuyo.dashboard.error.BusinessException;
import ar.edu.uncuyo.dashboard.pdf.PdfGenerator;
import ar.edu.uncuyo.dashboard.service.*;
import ar.edu.uncuyo.dashboard.txt.TxtImporter;
import com.itextpdf.text.DocumentException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

@Controller
@RequestMapping("/proveedores")
@RequiredArgsConstructor

public class ProveedorController {

    private final ProveedorService proveedorService;
    private final PdfGenerator pdfGenerator;


    private final String vistaLista = "/proveedor/proveedorLista";
    private final String vistaDetalle = "/proveedor/proveedorDetalle";
    private final String vistaAlta = "/proveedor/proveedorAlta";
    private final String vistaEdicion = "/proveedor/proveedorEdit";
    private final String redirectLista = "/proveedores";
    private final PaisService paisService;
    private final ProvinciaService provinciaService;
    private final DepartamentoService departamentoService;
    private final LocalidadService localidadService;

    @GetMapping("")
    public String listarProveedores(Model model) {
        return prepararVistaLista(model);
    }

    @GetMapping("/{id:\\d+}")
    public String detalleProveedor(Model model, @PathVariable Long id) {
        ProveedorDto proveedor = proveedorService.buscarProveedorDto(id);
        return prepararVistaDetalle(model, proveedor);
    }

    @GetMapping("/alta")
    public String altaProveedor(Model model) {
        return prepararVistaFormularioAlta(model, new ProveedorDto());
    }

    @GetMapping("/{id}/edit")
    public String modificarProveedor(Model model, @PathVariable Long id) {
        ProveedorDto proveedor = proveedorService.buscarProveedorDto(id);
        return prepararVistaFormularioEdicion(model, proveedor);
    }

    @PostMapping("/alta")
    public String altaProveedor(Model model, @Valid @ModelAttribute("proveedor") ProveedorDto proveedor, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return prepararVistaFormularioAlta(model, proveedor);

        try {
            proveedorService.crearProveedor(proveedor);
            return "redirect:" + redirectLista;
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessage());
            return prepararVistaFormularioAlta(model, proveedor);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            model.addAttribute("msgError", "Error de sistema");
            return prepararVistaFormularioAlta(model, proveedor);
        }
    }

    @PostMapping("/edit")
    public String modificarProveedor(Model model, @Valid @ModelAttribute("proveedor") ProveedorDto proveedor, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return prepararVistaFormularioEdicion(model, proveedor);

        try {
            proveedorService.modificarProveedor(proveedor);
            return "redirect:" + redirectLista;
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessage());
            return prepararVistaFormularioEdicion(model, proveedor);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            model.addAttribute("msgError", "Error de sistema");
            return prepararVistaFormularioEdicion(model, proveedor);
        }
    }

    @PostMapping("/{id}/baja")
    public String eliminarProveedor(Model model, @PathVariable Long id) {
        try {
            proveedorService.eliminarProveedor(id);
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

    @GetMapping("/pdf")
    public ResponseEntity<InputStreamResource> verPdf() throws FileNotFoundException, DocumentException {
        List<ProveedorDto> proveedores = proveedorService.listarProveedoresDtos();
        // Ruta del PDF generado

        String pdfPath = "proveedores.pdf";
        PdfGenerator.generarPdf(proveedores);

        InputStreamResource resource = new InputStreamResource(new FileInputStream(pdfPath));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=proveedores.pdf") // 👈 "inline" lo abre en el navegador
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    @GetMapping("/importar-txt")
    public String importarProveedores(Model model) {
        List<ProveedorDto> proveedores = proveedorService.importarDesdeTxt();
        model.addAttribute("proveedores", proveedores);
        return prepararVistaLista(model);
    }


    private String prepararVistaLista(Model model) {
        List<ProveedorDto> proveedores = proveedorService.listarProveedoresDtos();
        model.addAttribute("proveedores", proveedores);
        return vistaLista;
    }

    private void prepararVistaFormulario(Model model, ProveedorDto proveedorDto) {
        List<PaisDto> paises = paisService.listarPaisesDtos();
        model.addAttribute("paises", paises);

        Long paisId = proveedorDto.getDireccion().getPaisId();
        if (paisId != null) {
            List<ProvinciaDto> provincias = provinciaService.buscarProvinciaPorPais(paisId);
            model.addAttribute("provincias", provincias);
        }

        Long provinciaId = proveedorDto.getDireccion().getProvinciaId();
        if (provinciaId != null) {
            List<DepartamentoDto> departamentos = departamentoService.buscarDepartamentosPorProvincia(provinciaId);
            model.addAttribute("departamentos", departamentos);
        }

        Long departamentoId = proveedorDto.getDireccion().getDepartamentoId();
        if (departamentoId != null) {
            List<LocalidadDto> localidades = localidadService.buscarLocalidadesPorDepartamento(departamentoId);
            model.addAttribute("localidades", localidades);
        }

        model.addAttribute("proveedor", proveedorDto);
    }

    private String prepararVistaFormularioAlta(Model model, ProveedorDto proveedor) {
        prepararVistaFormulario(model, proveedor);
        return vistaAlta;
    }

    private String prepararVistaFormularioEdicion(Model model, ProveedorDto proveedor) {
        prepararVistaFormulario(model, proveedor);
        return vistaEdicion;
    }

    private String prepararVistaDetalle(Model model, ProveedorDto proveedor) {
        prepararVistaFormulario(model, proveedor);
        return vistaDetalle;
    }
}
