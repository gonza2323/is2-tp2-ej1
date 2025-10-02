package ar.edu.uncuyo.dashboard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "mensajes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_destinatario", nullable = false, length = 200)
    private String nombre;

    @Column(name = "email_destinatario", nullable = false, length = 320)
    private String correoElectronico;

    @Column(name = "titulo", nullable = false, length = 200)
    private String asunto;

    @Column(name = "texto", nullable = false, length = 4000)
    private String cuerpo;

    @Column(name = "fecha_programada")
    private LocalDateTime fechaProgramada;

    @Column(nullable = false)
    private boolean eliminado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;
}
