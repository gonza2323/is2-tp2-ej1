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

    @NotBlank(message = "Debe indicar un código postal")
    @Size(max = 20, message = "Máximo 20 caracteres")
    private String codigoPostal;

    @NotNull(message = "Debe indicar el departamento")
    private Long departamentoId;
}
