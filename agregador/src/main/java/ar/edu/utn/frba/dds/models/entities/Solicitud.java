package ar.edu.utn.frba.dds.models.entities;

import ar.edu.utn.frba.dds.models.entities.enums.TipoEstado;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "solicitud")
public class Solicitud {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column
  @Getter
  @Setter
  private String titulo;
  @Column(length = 2000)
  @Getter
  @Setter
  private String texto;
  @Column
  @Setter
  @Getter
  private LocalDateTime fecha;
  @Getter @Setter
  @Column
  private String creador = " ";
  //one to many
  @Getter
  @Setter
  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "estado_id", referencedColumnName = "id")
  private Estado estadoActual;
  @Getter
  @OneToMany(mappedBy = "solicitud")
  private List<Estado> historial = new ArrayList<>();
  @Column
  private Integer spam;

  @ManyToOne
  @JoinColumn(name="hecho_id", referencedColumnName = "id")
  private Hecho hecho;

  public Solicitud() {
    Estado estado = new Estado();
    estado.setSolicitud(this);
    this.estadoActual = estado;
    this.fecha = LocalDateTime.now();
    this.spam = 0;
  }

  public Boolean estaFundado() {
    return texto.length() >= 500;
  }

  public void rechazar() {
    Estado nuevoEstado = new Estado();
    nuevoEstado.setEstado(TipoEstado.RECHAZADA);
    this.cambiarEstado(nuevoEstado);
  }

  public void aceptar() {
    Estado nuevoEstado = new Estado();
    nuevoEstado.setEstado(TipoEstado.ACEPTADA);
    this.cambiarEstado(nuevoEstado);
  }

  private void cambiarEstado(Estado estado) {
    this.historial.add(this.estadoActual);
    estado.setSolicitud(this);
    this.estadoActual = estado;
  }

  public boolean estaAceptada() {
    return this.estadoActual.getEstado() == TipoEstado.ACEPTADA;
  }

  public void marcarSpam() {
    this.spam = 1;
  }
  public boolean esSpam() {
    return this.spam == 1;
  }
}