package ar.edu.uncuyo.dashboard.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioCreateFormDto {
    @NotEmpty(message = "Debe indicar una contraseña")
    @Size(min = 8, max = 255, message = "La contraseña debe tener entre 8 y 255 caracteres")
    private String clave;

    @NotEmpty(message = "Debe confirmar su contraseña")
    private String confirmacionClave;

    @Valid
    private PersonaDto persona;
}
