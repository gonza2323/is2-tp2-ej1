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
public class EmpresaDto {
    private Long id;

    @NotBlank(message = "La razón social no puede estar vacía")
    @Size(max = 100, message = "Máximo 100 caracteres")
    private String razonSocial;

    @Valid
    private DireccionDto direccion = new DireccionDto();

    private String nombreLocalidad;
    private String nombreDepartamento;
    private String nombreProvincia;
    private String nombrePais;
}