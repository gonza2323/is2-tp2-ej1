package ar.edu.uncuyo.dashboard.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Localidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String codigoPostal;

    private boolean eliminado;

    @JoinColumn(nullable = false)
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private Departamento departamento;
}
