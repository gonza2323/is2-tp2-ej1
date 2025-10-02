package ar.edu.uncuyo.dashboard.jobs;

import ar.edu.uncuyo.dashboard.dto.MensajeDTO;
import ar.edu.uncuyo.dashboard.dto.ProveedorDto;
import ar.edu.uncuyo.dashboard.service.MensajeService;
import ar.edu.uncuyo.dashboard.service.ProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EndOfYearGreetingJob {

    private final ProveedorService proveedorService;
    private final MensajeService mensajeService;

    // Configuración externa por application.properties
    @Value("${app.mensajes.finanno.cta:https://www.uncuyo.edu.ar/}")
    private String ctaUrl;

    @Value("${app.mensajes.finanno.enabled:true}")
    private boolean enabled;

    @Value("${app.mensajes.finanno.zone:America/Argentina/Buenos_Aires}")
    private String zoneId;

    @Value("${app.mensajes.finanno.titulo:¡Feliz Año Nuevo!}")
    private String titulo;

    @Value("${app.mensajes.finanno.texto:Gracias por acompañarnos este año. ¡Que el próximo sea aún mejor!}")
    private String texto;

    /**
     * Ejecuta el 31 de diciembre a las 8:00 AM hora local.
     * cron: segundo minuto hora díaMes mes díaSemana
     *       0 0 8 31 12 *   → 31 de diciembre a las 08:00
     */
    @Scheduled(cron = "${app.mensajes.finanno.cron:0 0 8 31 12 *}",
            zone = "${app.mensajes.finanno.zone:America/Argentina/Buenos_Aires}")
    @Transactional
    public void enviarSaludosFinDeAnno() {
        if (!enabled) {
            return;
        }

        List<ProveedorDto> proveedores = proveedorService.listarProveedoresDtos();

        for (ProveedorDto proveedor : proveedores) {
            if (proveedor.getPersona() == null ||
                    !StringUtils.hasText(proveedor.getPersona().getCorreoElectronico())) {
                continue; // ignorar proveedor sin email
            }

            MensajeDTO dto = new MensajeDTO();
            String nombreCompleto = (proveedor.getPersona().getNombre() + " " + proveedor.getPersona().getApellido()).trim();

            dto.setNombre(nombreCompleto);
            dto.setCorreoElectronico(proveedor.getPersona().getCorreoElectronico());
            dto.setAsunto(titulo);
            dto.setCuerpo(texto);
            dto.setFechaProgramada(LocalDateTime.now(ZoneId.of(zoneId)));
            dto.setProveedorId(proveedor.getId());

            mensajeService.crear(dto);
        }
    }
}
