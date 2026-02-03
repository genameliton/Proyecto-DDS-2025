package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.enums.Formato;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "multimedia")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Multimedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "ruta")
    private String ruta;

    @Enumerated(EnumType.STRING)
    @Column(name = "formato")
    private Formato formato;
}
