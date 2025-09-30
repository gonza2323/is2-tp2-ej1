package ar.edu.uncuyo.dashboard.dto;

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
public class CambiarClaveFormDto {
    @NotEmpty(message = "Debe indicar su contraseña actual")
    private String claveActual;

    @NotEmpty(message = "Debe indicar una nueva contraseña")
    @Size(min = 8, max = 255, message = "La contraseña debe tener entre 8 y 255 caracteres")
    private String nuevaClave;

    @NotEmpty(message = "Debe confirmar su contraseña")
    private String confirmacionClave;
}
