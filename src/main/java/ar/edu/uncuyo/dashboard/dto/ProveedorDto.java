package ar.edu.uncuyo.dashboard.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorDto {
    private Long id;

    @NotBlank(message = "Debe indicar el CUIT")
    @Size(min = 11, max = 11, message = "El CUIT debe tener 11 dígitos")
    private String cuit;

    @Valid
    private PersonaDto persona = new PersonaDto();

    @Valid
    private DireccionDto direccion = new DireccionDto();

    private String nombrePais;
    private String nombreProvincia;
}
