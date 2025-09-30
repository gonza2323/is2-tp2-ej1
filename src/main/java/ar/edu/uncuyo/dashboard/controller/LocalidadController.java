package ar.edu.uncuyo.dashboard.controller;

import ar.edu.uncuyo.dashboard.dto.LocalidadDto;
import ar.edu.uncuyo.dashboard.service.LocalidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class LocalidadController {
    private final LocalidadService localidadService;

    @ResponseBody
    @GetMapping("/localidades")
    List<LocalidadDto> buscarProvinciasPorPais(@Param("departamentoId") Long departamentoId) {
        return localidadService.buscarLocalidadesPorDepartamento(departamentoId);
    }
}
