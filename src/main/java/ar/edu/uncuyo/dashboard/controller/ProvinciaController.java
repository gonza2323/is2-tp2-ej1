package ar.edu.uncuyo.dashboard.controller;

import ar.edu.uncuyo.dashboard.dto.ProvinciaDto;
import ar.edu.uncuyo.dashboard.service.ProvinciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProvinciaController {
    private final ProvinciaService provinciaService;

    @ResponseBody
    @GetMapping("/provincias")
    List<ProvinciaDto> buscarProvinciasPorPais(@Param("paisId") Long paisId) {
        return provinciaService.buscarProvinciaPorPais(paisId);
    }
}
