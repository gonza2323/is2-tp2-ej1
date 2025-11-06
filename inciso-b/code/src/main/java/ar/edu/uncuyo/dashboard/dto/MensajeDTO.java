package ar.edu.uncuyo.dashboard.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class MensajeDTO {
    private Long id;

    @NotBlank
    @Size(max = 200)
    private String nombre;

    @NotBlank
    @Email
    @Size(max = 320)
    private String correoElectronico;

    @NotBlank
    @Size(max = 200)
    private String asunto;

    @NotBlank
    @Size(max = 4000)
    private String cuerpo;

    private LocalDateTime fechaProgramada;

    @NotNull
    private Long proveedorId;
}
