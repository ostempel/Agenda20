package edu.hm.cs.katz.swt2.agenda.persistence;

import edu.hm.cs.katz.swt2.agenda.common.StatusEnum;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

/**
 * Modellklasse für die Speicherung der Beziehung zwischen Anwender und einem konkreten Task.
 * Enthält die Abbildung auf eine Datenbanktabelle in Form von JPA-Annotation.
 * 
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
@Entity
public class Status {

  // Die Verwendung einer generierten ID ist deutlich einfacher zu verstehen als die
  // sauberere Lösung, aus Anwender und Task einen zusammengesetzten Schlüssel zu erstellen.
  @Id
  @NotNull
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @ManyToOne
  @NotNull
  private User user;

  @ManyToOne
  @NotNull
  private Task task;

  @NotNull
  @Column(length = 250)
  @Length(max = 250)
  private String comment = "";

  @Enumerated(EnumType.STRING)
  @NotNull
  private StatusEnum status = StatusEnum.NEU;

  /**
   * JPA-kompatibler Kostruktor. Wird nur von JPA verwendet und darf private sein.
   */
  public Status() {
    // JPA benötigt einen Default-Konstruktor!
  }

  /**
   * Konstruktor zum Erstellen eines neuen Status.
   * 
   * @param task Task, darf nicht <code>null</code> sein
   * @param user Anwender, darf nicht <code>null</code> sein
   */
  public Status(final Task task, final User user) {
    this.task = task;
    this.user = user;
  }

  @Override
  public String toString() {
    return "Status " + status;
  }

  public Long getId() {
    return id;
  }

  public User getUser() {
    return user;
  }

  public StatusEnum getStatus() {
    return status;
  }

  public void setStatus(final StatusEnum status) {
    this.status = status;
  }

  public Task getTask() {
    return task;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  /*
   * Standard-Methoden. Es ist sinnvoll, hier auf die Auswertung der Assoziationen zu verzichten,
   * nur die Primärschlüssel zu vergleichen und insbesonderen Getter zu verwenden, um auch mit den
   * generierten Hibernate-Proxys kompatibel zu bleiben.
   */

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Status)) {
      return false;
    }
    Status other = (Status) obj;
    return Objects.equals(getId(), other.getId());
  }
}
