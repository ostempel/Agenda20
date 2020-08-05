package edu.hm.cs.katz.swt2.agenda.persistence;

import java.util.Collection;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

/**
 * Modellklasse für die Speicherung der Aufgaben. Enthält die Abbildung auf eine Datenbanktabelle in
 * Form von JPA-Annotation.
 * 
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
@Entity
public class Task {

  @Id
  @NotNull
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @NotNull
  @Length(min = 8, max = 32)
  @Column(length = 32)
  private String title;

  @NotNull
  @Column(length = 60)
  @Length(min = 10, max = 60)
  private String shortDescription;

  @NotNull
  @Column(length = 160)
  @Length(min = 10, max = 160)
  private String longDescription;

  @NotNull
  @ManyToOne
  private Topic topic;

  @NotNull
  @Column
  private String deadline;

  @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
  private Collection<Status> status;

  /**
   * JPA-kompatibler Kostruktor. Wird nur von JPA verwendet und darf private sein.
   */
  public Task() {
    // JPA benötigt einen Default-Konstruktor!
  }

  /**
   * Konstruktor zum Erstellen eines neuen Tasks.
   * 
   * @param topic Id zu welchem Topic der Task gehört.
   * @param title Titel, zwischen 10 und 60 Zeichen.
   * @param shortDescription Kurzbeschreibung, zwischen 10 und 60 Zeichen.
   * @param longDescription Langbeschreibung, zwischen 10 und 160 Zeichen.
   * @param deadline Datum für einen Endtermin.
   */
  public Task(final Topic topic, final String title, final String shortDescription,
      final String longDescription, String deadline) {
    this.topic = topic;
    topic.addTask(this);
    this.title = title;
    this.shortDescription = shortDescription;
    this.longDescription = longDescription;
    this.deadline = deadline;
  }

  @Override
  public String toString() {
    return "Task \"" + title + "\"";
  }

  public Long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public Topic getTopic() {
    return topic;
  }

  public String getShortDescription() {
    return shortDescription;
  }

  public String getLongDescription() {
    return longDescription;
  }

  public void setShortDescription(String shortDescription) {
    this.shortDescription = shortDescription;
  }

  public void setLongDescription(String longDescription) {
    this.longDescription = longDescription;
  }

  public String getDeadline() {
    return deadline;
  }

  public void setDeadline(String deadline) {
    this.deadline = deadline;
  }

  /*
   * Standard-Methoden. Es ist sinnvoll, hier auf die Auswertung der Assoziationen zu verzichten,
   * nur die Primärschlüssel zu vergleichen und insbesonderen Getter zu verwenden, um auch mit den
   * generierten Hibernate-Proxys kompatibel zu bleiben.
   */

  @Override
  public int hashCode() {
    return Objects.hash(id, topic);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Task)) {
      return false;
    }
    Task other = (Task) obj;
    return Objects.equals(getId(), other.getId());
  }
}
