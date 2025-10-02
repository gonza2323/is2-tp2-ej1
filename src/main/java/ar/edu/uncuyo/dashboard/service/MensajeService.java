package ar.edu.uncuyo.dashboard.service;

import ar.edu.uncuyo.dashboard.dto.MensajeDTO;
import ar.edu.uncuyo.dashboard.dto.ProveedorDto;
import ar.edu.uncuyo.dashboard.dto.PersonaDto;
import ar.edu.uncuyo.dashboard.entity.Mensaje;
import ar.edu.uncuyo.dashboard.entity.Proveedor;
import ar.edu.uncuyo.dashboard.mapper.MensajeMapper;
import ar.edu.uncuyo.dashboard.repository.MensajeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.util.HtmlUtils;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MensajeService {


    private final MensajeRepository mensajeRepository;
    private final ProveedorService proveedorService;
    private final MensajeMapper mensajeMapper;
    private final JavaMailSender mailSender;
    private final TaskScheduler taskScheduler;

    @Value("${app.mensajes.cta:https://www.uncuyo.edu.ar/}")
    private String ctaUrl;


    private static final String REMITENTE = "gimnasiosport21@gmail.com";

    @Transactional(readOnly = true)
    public List<Mensaje> listar(MensajeDTO filtro) {
        return mensajeRepository.findAllByEliminadoFalse();
    }

    @Transactional(readOnly = true)
    public Mensaje obtener(Long id) {
        return mensajeRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Mensaje no encontrado"));
    }

    @Transactional
    public Mensaje crear(MensajeDTO dto) {
        normalizarDto(dto);
        validarMensaje(dto);

        Proveedor proveedor = proveedorService.buscarProveedor(dto.getProveedorId());

        Mensaje m = mensajeMapper.toEntity(dto);
        m.setProveedor(proveedor);
        m.setEliminado(false);

        normalizarCamposMensaje(m);

        Mensaje guardado = mensajeRepository.save(m);

        enviarCorreo(dto, proveedor);

        mensajeRepository.save(m);
        return guardado;
    }

    @Transactional
    public Mensaje actualizar(Long id, MensajeDTO dto) {
        normalizarDto(dto);
        validarMensaje(dto);

        Mensaje m = obtener(id);
        mensajeMapper.updateEntityFromDto(dto, m);

        if (dto.getProveedorId() != null &&
                (m.getProveedor() == null || !dto.getProveedorId().equals(m.getProveedor().getId()))) {
            Proveedor proveedor = proveedorService.buscarProveedor(dto.getProveedorId());
            m.setProveedor(proveedor);
        }

        normalizarCamposMensaje(m);

        Mensaje guardado = mensajeRepository.save(m);

        enviarCorreo(dto, m.getProveedor());

        return guardado;
    }

    @Transactional(readOnly = true)
    public MensajeDTO toDto(Mensaje mensaje) {
        return mensajeMapper.toDto(mensaje);
    }

    @Transactional
    public void eliminarLogico(Long id) {
        Mensaje m = obtener(id);
        m.setEliminado(true);
        mensajeRepository.save(m);
    }

    public void enviarCorreo(MensajeDTO dto, Proveedor proveedor) {
        if (dto == null || proveedor == null) return;

        ProveedorDto proveedorDto = proveedorService.buscarProveedorDto(proveedor.getId());
        PersonaDto persona = proveedorDto.getPersona();

        String destinatario = (persona != null && StringUtils.hasText(persona.getCorreoElectronico()))
                ? persona.getCorreoElectronico()
                : dto.getCorreoElectronico();

        if (!StringUtils.hasText(destinatario)) {
            log.warn("Proveedor {} no tiene email de contacto válido", proveedor.getId());
            return;
        }

        String nombre = (persona != null)
                ? ( (StringUtils.hasText(persona.getNombre()) ? persona.getNombre() : "") + " " +
                (StringUtils.hasText(persona.getApellido()) ? persona.getApellido() : "") ).trim()
                : (StringUtils.hasText(dto.getNombre()) ? dto.getNombre() : "Proveedor");

        String subject = StringUtils.hasText(dto.getAsunto()) ? dto.getAsunto() : "Mensaje Gimnasio Sport";
        String cuerpo = StringUtils.hasText(dto.getCuerpo()) ? dto.getCuerpo()
                : "Hola! Queríamos compartir una novedad contigo.";

        Runnable tarea = () -> {
            try {
                MimeMessage mime = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mime, MimeMessageHelper.MULTIPART_MODE_NO, StandardCharsets.UTF_8.name());
                helper.setFrom(REMITENTE);
                helper.setTo(destinatario);
                helper.setSubject(subject);

                // Usamos el template HTML genérico
                helper.setText(buildHtmlTemplate(nombre, cuerpo, subject, ctaUrl), true);

                mailSender.send(mime);
            } catch (Exception ex) {
                log.warn("No se pudo enviar el correo HTML a {}", destinatario, ex);
            }
        };

        if (dto.getFechaProgramada() != null) {
            Instant instante = dto.getFechaProgramada().atZone(ZoneId.systemDefault()).toInstant();
            taskScheduler.schedule(tarea, Date.from(instante));
        } else {
            taskScheduler.schedule(tarea, new Date()); // envío inmediato
        }
    }


    private void validarMensaje(MensajeDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("El mensaje no puede ser nulo");
        }
        if (dto.getProveedorId() == null) {
            throw new IllegalArgumentException("Debe indicarse el proveedor asociado al mensaje");
        }
    }

    private void normalizarDto(MensajeDTO dto) {
        if (dto == null) return;

        if (!StringUtils.hasText(dto.getNombre())) {
            dto.setNombre("Destinatario");
        }
        if (!StringUtils.hasText(dto.getAsunto())) {
            dto.setAsunto("Mensaje Gimnasio Sport");
        }
        if (!StringUtils.hasText(dto.getCuerpo())) {
            dto.setCuerpo("Hola! Queríamos compartir una novedad contigo.");
        }
    }

    private void normalizarCamposMensaje(Mensaje mensaje) {
        if (mensaje == null) return;

        mensaje.setNombre(normalizarCadena(mensaje.getNombre()));
        mensaje.setCorreoElectronico(normalizarCadena(mensaje.getCorreoElectronico()));
        mensaje.setAsunto(normalizarCadena(mensaje.getAsunto()));
        mensaje.setCuerpo(normalizarCadena(mensaje.getCuerpo()));
    }

    private String normalizarCadena(String valor) {
        return valor == null ? null : valor.trim();
    }

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
                        Ir al sitio
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
