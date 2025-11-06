package ar.edu.uncuyo.dashboard.controller;

import ar.edu.uncuyo.dashboard.dto.MensajeDTO;
import ar.edu.uncuyo.dashboard.service.MensajeService;
import ar.edu.uncuyo.dashboard.service.ProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/mensajes")
@RequiredArgsConstructor
public class MensajeController {

    private final MensajeService mensajeService;
    private final ProveedorService proveedorService;

    private static final String VIEW_LIST = "mensaje/list";
    private static final String VIEW_PROGRAMAR = "mensaje/programar";
    private static final String REDIRECT_MENSAJES = "redirect:/mensajes";

    @GetMapping()
    public String listar(@ModelAttribute("f") MensajeDTO filtro, Model model) {
        List<MensajeDTO> mensajes = mensajeService.listar(filtro).stream()
                .map(mensajeService::toDto)
                .collect(Collectors.toList());
        model.addAttribute("mensajes", mensajes);
        return VIEW_LIST;
    }

    @GetMapping("/programar")
    public String programar(@RequestParam(value = "id", required = false) Long id,
                            @RequestParam(value = "readonly", defaultValue = "false") boolean readOnly,
                            Model model) {

        MensajeDTO dto = (id == null)
                ? nuevoMensaje()
                : mensajeService.toDto(mensajeService.obtener(id));

        if (dto.getFechaProgramada() == null) {
            dto.setFechaProgramada(LocalDateTime.now().plusHours(1));
        }

        model.addAttribute("mensajeDto", dto);
        model.addAttribute("readOnly", readOnly);

        model.addAttribute("proveedores", proveedorService.listarProveedoresDtos());

        return VIEW_PROGRAMAR;
    }

    @PostMapping("/programar")
    public String guardarProgramacion(@ModelAttribute("mensajeDto") MensajeDTO dto, Model model) {

        if (dto.getProveedorId() == null) {
            model.addAttribute("msgError", "Debe seleccionar un proveedor para enviar el mensaje");
            return VIEW_PROGRAMAR;
        }

        if (dto.getFechaProgramada() == null) {
            dto.setFechaProgramada(LocalDateTime.now());
        }

        if (dto.getId() == null) {
            mensajeService.crear(dto);
        } else {
            mensajeService.actualizar(dto.getId(), dto);
        }

        model.addAttribute("msgExito", "Mensaje guardado correctamente");
        return REDIRECT_MENSAJES;
    }

    @GetMapping("/{id}")
    public String ver(@PathVariable Long id, Model model) {
        MensajeDTO dto = mensajeService.toDto(mensajeService.obtener(id));
        model.addAttribute("mensajeDto", dto);
        model.addAttribute("readOnly", true);


        model.addAttribute("proveedores", proveedorService.listarProveedoresDtos());

        return VIEW_PROGRAMAR;
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        mensajeService.eliminarLogico(id);
        return REDIRECT_MENSAJES;
    }

    private MensajeDTO nuevoMensaje() {
        return new MensajeDTO();
    }
}
