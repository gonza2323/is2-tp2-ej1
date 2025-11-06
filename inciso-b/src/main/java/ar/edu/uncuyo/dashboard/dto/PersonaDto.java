package ar.edu.uncuyo.dashboard.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PersonaDto {
    Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 50, message = "Máximo 50 caracteres")
    String nombre;

    @NotBlank(message = "El apellido no puede estar vacío")
    @Size(max = 50, message = "Máximo 50 caracteres")
    String apellido;

    @Size(max = 20, message = "Máximo 20 caracteres")
    String telefono;

    @NotBlank(message = "Debe indicar una dirección de correo")
    @Email(message = "Debe especificar un email válido")
    String correoElectronico;
}
