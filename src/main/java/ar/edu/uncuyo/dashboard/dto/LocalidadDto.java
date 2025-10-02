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
public class LocalidadDto {
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 100, message = "Máximo 100 caracteres")
    private String nombre;

    @Size(min = 4, max = 10, message = "Entre 4 y 10 caracteres")
    private String codigoPostal;

    @NotNull(message = "Debe indicar el departamento")
    private Long departamentoId;

    @NotNull(message = "Debe indicar la provincia")
    private Long provinciaId;

    @NotNull(message = "Debe indicar el país")
    private Long paisId;
}
