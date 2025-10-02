package ar.edu.uncuyo.dashboard.jobs;

import ar.edu.uncuyo.dashboard.dto.UsuarioDto;
import ar.edu.uncuyo.dashboard.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionSchedulerService {

    private final UsuarioService usuarioService;
    private final JavaMailSender mailSender;

    private static final String REMITENTE = "gimnasiosport21@gmail.com";
    private static final String CTA_URL = "https://www.uncuyo.edu.ar/";

    /**
     * Ejecuta el día 5 de cada mes a las 10:00 AM (hora local).
     */
    @Scheduled(cron = "0 40 12 * * *", zone = "America/Argentina/Buenos_Aires")
    public void enviarPromocionMensual() {
        log.info("Iniciando envío de promociones mensuales a usuarios...");

        List<UsuarioDto> usuarios = usuarioService.listarUsuariosDtos();

        for (UsuarioDto usuario : usuarios) {
            if (!StringUtils.hasText(usuario.getCorreoElectronico())) {
                continue;
            }
            enviarPromocionAsync(
                    usuario.getCorreoElectronico(),
                    usuario.getNombre(),
                    usuario.getApellido()
            );
        }
    }

    /**
     * Envío asíncrono con template HTML.
     */
    @Async
    public void enviarPromocionAsync(String email, String nombre, String apellido) {
        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mime,
                    MimeMessageHelper.MULTIPART_MODE_NO,
                    StandardCharsets.UTF_8.name()
            );

            helper.setFrom(REMITENTE);
            helper.setTo(email);
            helper.setSubject("Promoción Gimnasio Sport");

            String contenidoHtml = buildHtmlTemplate(
                    (StringUtils.hasText(nombre) ? nombre : "") + " " +
                            (StringUtils.hasText(apellido) ? apellido : ""),
                    "¡Tenemos una promoción especial para vos este mes!",
                    "Promoción Gimnasio Sport",
                    CTA_URL
            );

            helper.setText(contenidoHtml, true);
            mailSender.send(mime);

            log.info("Promoción enviada a {}", email);
        } catch (Exception ex) {
            log.error("Error al enviar promoción a {}: {}", email, ex.getMessage());
        }
    }

    // =========================================================
    // Template HTML (igual que en MensajeService)
    // =========================================================
    private String buildHtmlTemplate(String nombre, String mensaje, String asunto, String url) {
        String safeNombre = HtmlUtils.htmlEscape(nombre == null ? "" : nombre);
        String safeMensaje = HtmlUtils.htmlEscape(mensaje == null ? "" : mensaje);
        String safeAsunto = HtmlUtils.htmlEscape(asunto == null ? "" : "Mensaje");

        return """
        <!doctype html>
        <html lang="es">
        <head>
          <meta charset="utf-8">
          <meta http-equiv="x-ua-compatible" content="ie=edge">
          <meta name="viewport" content="width=device-width, initial-scale=1">
          <title>%s</title>
        </head>
        <body style="margin:0;padding:0;background:#f5f7fb;font-family:Arial,Helvetica,sans-serif;">
          <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background:#f5f7fb;padding:24px 0;">
            <tr>
              <td align="center">
                <table role="presentation" width="600" cellpadding="0" cellspacing="0" style="max-width:600px;background:#ffffff;border-radius:12px;overflow:hidden;box-shadow:0 2px 8px rgba(0,0,0,.06);">
                  <tr>
                    <td style="background:#4e73df;height:8px;"></td>
                  </tr>
                  <tr>
                    <td style="padding:28px 32px 8px 32px;">
                      <h1 style="margin:0 0 8px 0;font-size:22px;color:#1f2937;">Hola %s,</h1>
                      <p style="margin:0;font-size:15px;line-height:22px;color:#374151;">%s</p>
                    </td>
                  </tr>
                  <tr>
                    <td style="padding:20px 32px 8px 32px;">
                      <a href="%s" target="_blank"
                         style="display:inline-block;text-decoration:none;padding:12px 20px;border-radius:8px;background:#1cc88a;color:#ffffff;font-weight:bold;font-size:14px;">
                        Ver más
                      </a>
                    </td>
                  </tr>
                  <tr>
                    <td style="padding:20px 32px 28px 32px;color:#6b7280;font-size:12px;line-height:18px;">
                      <p style="margin:0;">Enviado automáticamente por Gimnasio Sport</p>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
          </table>
        </body>
        </html>
        """.formatted(safeAsunto, safeNombre, safeMensaje, url);
    }
}
