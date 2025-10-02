package ar.edu.uncuyo.dashboard.service;

import ar.edu.uncuyo.dashboard.dto.UsuarioDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionSchedulerService {

    private final UsuarioService usuarioService; // 👈 ajusta al nombre real de tu servicio de usuarios
    private final JavaMailSender mailSender;

    private static final String REMITENTE = "gimnasiosport21@gmail.com";

    /**
     * Enviar promoción el 5 de cada mes a las 10:00 AM.
     * Cron: s m H d M w
     *       0 0 10 5 * *
     */
    @Scheduled(cron = "0 0 10 5 * *", zone = "America/Argentina/Buenos_Aires")
    public void enviarPromocionMensual() {
        log.info("Iniciando envío de promociones mensuales a usuarios...");

        List<UsuarioDto> usuarios = usuarioService.listarUsuariosDtos();

        for (UsuarioDto usuario : usuarios) {
            if (!StringUtils.hasText(usuario.getCorreoElectronico())) {
                continue;
            }
            enviarPromocionAsync(usuario.getCorreoElectronico(), usuario.getNombre(), usuario.getApellido());
        }
    }

    /**
     * Envío asíncrono de una promoción a un usuario.
     */
    @Async
    public void enviarPromocionAsync(String email, String nombre, String apellido) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(REMITENTE);
            message.setTo(email);
            message.setSubject("Promoción Gimnasio Sport");
            message.setText(String.format(
                    "Hola %s %s,\n\n¡Tenemos una promoción especial para vos este mes!\n\nSaludos,\nGimnasio Sport",
                    StringUtils.hasText(nombre) ? nombre : "",
                    StringUtils.hasText(apellido) ? apellido : ""
            ));

            mailSender.send(message);
            log.info("Promoción enviada a {}", email);
        } catch (Exception ex) {
            log.error("Error al enviar promoción a {}: {}", email, ex.getMessage());
        }
    }
}
