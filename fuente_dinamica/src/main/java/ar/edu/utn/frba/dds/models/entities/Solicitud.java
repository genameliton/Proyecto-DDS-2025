package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.enums.EstadoSolicitud;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "solicitud")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Solicitud {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "texto", columnDefinition = "TEXT")
    private String texto;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "hecho_id", referencedColumnName = "id")
    private Hecho hecho;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    @Builder.Default
    private EstadoSolicitud estadoSolicitud = EstadoSolicitud.PENDIENTE;

    @Column(name = "fecha")
    @Builder.Default
    private LocalDateTime fecha = LocalDateTime.now();

    @Column(name = "responsable")
    private String responsable;

    @Column(name = "supervisor")
    private String supervisor;

    public Boolean estaFundado() {
    return texto.length() >= 500;
  }

    public void rechazar() {
        this.estadoSolicitud = EstadoSolicitud.RECHAZADA; // TODO: Chequear esto.
    }

    public void aceptar() {
        this.estadoSolicitud = EstadoSolicitud.ACEPTADA; // TODO: Chequear esto.
    }
}
