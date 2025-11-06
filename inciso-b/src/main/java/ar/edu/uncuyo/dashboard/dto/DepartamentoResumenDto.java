package ar.edu.uncuyo.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartamentoResumenDto {
    private Long id;

    private String nombre;

    private Long paisId;

    private Long provinciaId;

    private String paisNombre;

    private String provinciaNombre;
}
