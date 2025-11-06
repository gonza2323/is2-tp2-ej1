package ar.edu.uncuyo.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocalidadResumenDto {
    private Long id;

    private String nombre;

    private String codigoPostal;

    private Long paisId;

    private Long provinciaId;

    private Long departamentoId;

    private String paisNombre;

    private String provinciaNombre;

    private String departamentoNombre;
}
