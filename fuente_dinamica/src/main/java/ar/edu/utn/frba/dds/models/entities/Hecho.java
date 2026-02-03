package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.enums.EstadoHecho;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "hechos")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Hecho {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "titulo", nullable = false)
  private String titulo;

  @Column(name = "descripcion", nullable = false)
  private String descripcion;

  @Column(name = "categoria", nullable = false)
  private String categoria;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "ubicacion_id", referencedColumnName = "id")
  private Ubicacion ubicacion;

  @Column(name = "fecha_acontecimiento")
  private LocalDateTime fechaAcontecimiento;

  @Builder.Default
  @Column(name = "fecha_carga")
  private LocalDateTime fechaCarga = LocalDateTime.now();

  @Column(name = "fecha_ultima_modificacion")
  private LocalDateTime fechaUltimaModificacion;

  @Column(name = "nombre_autor")
  private String nombreAutor;

  @Builder.Default
  @OneToMany
  @JoinColumn(name = "hecho_id", referencedColumnName = "id")
  private List<Multimedia> multimedia = new ArrayList<>(List.of());

  @Enumerated(EnumType.STRING)
  @Column(name = "estado", nullable = false)
  @Builder.Default
  private EstadoHecho estadoHecho = EstadoHecho.PENDIENTE;

  @Column(name = "motivo_rechazo", columnDefinition = "TEXT")
  private String motivoRechazo;

  @Column(name = "sugerencias", columnDefinition = "TEXT")
  private String sugerencias;

  @Column(name = "revisado_por")
  private String revisadoPor;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Hecho hecho)) {
      return false;
    }
    return titulo.equals(hecho.titulo);
  }

  /*
   * public boolean addEtiqueta(Etiqueta etiqueta) {
   * return this.etiquetas.add(etiqueta);
   * }
   */

  public void addMultimedia(Multimedia multimediaNueva) {
    multimedia.add(multimediaNueva);
  }

  public boolean estaAceptado() {
    return this.estadoHecho == EstadoHecho.ACEPTADO || this.estadoHecho == EstadoHecho.ACEPTADO_CON_SUGERENCIAS;
  }

  public void aceptar(String supervisor) {
    this.estadoHecho = EstadoHecho.ACEPTADO;
    this.revisadoPor = supervisor;
  }

  public void aceptarConSugerencias(String supervisor, String sugerencias) {
    this.estadoHecho = EstadoHecho.ACEPTADO_CON_SUGERENCIAS;
    this.revisadoPor = supervisor;
    this.sugerencias = sugerencias;
  }

  public void rechazar(String supervisor, String motivoRechazo) {
    this.estadoHecho = EstadoHecho.RECHAZADO;
    this.revisadoPor = supervisor;
    this.motivoRechazo = motivoRechazo;
  }

  public boolean estaPendiente() {
    return this.estadoHecho == EstadoHecho.PENDIENTE;
  }

  public boolean estaRechazado() {
    return this.estadoHecho == EstadoHecho.RECHAZADO;
  }
}
