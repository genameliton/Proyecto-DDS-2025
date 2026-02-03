package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.entities.enums.TipoEstado;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
@Entity
@Table(name = "estado")
public class Estado {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column
  private String supervisor;
  @Enumerated(EnumType.STRING)
  @Column
  private TipoEstado estado;
  @Column
  private LocalDateTime fechaActualizacion;
  @Setter
  @ManyToOne
  @JoinColumn(name = "solicitud_id", referencedColumnName = "id")
  private Solicitud solicitud;
  public Estado() {
    this.fechaActualizacion = LocalDateTime.now();
    this.estado = TipoEstado.PENDIENTE;
  }

}
