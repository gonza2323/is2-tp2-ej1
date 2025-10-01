package ar.edu.uncuyo.dashboard.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Direccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String calle;

    @Column(nullable = false)
    private String numeracion;

    private String barrio;

    private String manzanaPiso;

    private String casaDepartamento;

    private String referencia;

    private Double latitud;

    private Double longitud;

    private boolean eliminado;

    @JoinColumn(nullable = false)
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private Localidad localidad;
}
