package ar.edu.uncuyo.dashboard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DireccionDto {
    private Long id;

    @NotBlank(message = "Debe indicar la calle")
    @Size(max = 50, message = "Máximo 50 caracteres")
    private String calle;

    @NotBlank(message = "Debe indicar la numeración")
    @Size(max = 10, message = "Máximo 10 caracteres")
    private String numeracion;

    @Size(max = 50, message = "Máximo 50 caracteres")
    private String barrio;

    @Size(max = 10, message = "Máximo 10 caracteres")
    private String manzanaPiso;

    @Size(max = 10, message = "Máximo 10 caracteres")
    private String casaDepartamento;

    @Size(max = 50, message = "Máximo 50 caracteres")
    private String referencia;

    @NotNull(message = "Debe seleccionar un país")
    private Long paisId;

    @NotNull(message = "Debe seleccionar una provincia")
    private Long provinciaId;

    @NotNull(message = "Debe seleccionar un departamento")
    private Long departamentoId;

    @NotNull(message = "Debe seleccionar una localidad")
    private Long localidadId;
}
