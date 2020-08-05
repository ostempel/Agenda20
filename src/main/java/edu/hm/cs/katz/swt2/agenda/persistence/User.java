package edu.hm.cs.katz.swt2.agenda.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

/**
 * Modellklasse für die Speicherung von Anwenderdaten. Enthält die Abbildung auf eine
 * Datenbanktabelle in Form von JPA-Annotation.
 * 
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
@Entity
public class User {

  @Id
  @NotNull
  @Length(min = 4, max = 32)
  @Column(length = 32)
  private String login;

  @NotNull
  @Length(min = 4, max = 32)
  @Column(length = 32)
  private String name;

  @NotNull
  @Length(min = 7, max = 32) // lenght includes "{noop}"
  @Column(length = 32)
  private String password;
  
  @NotNull
  @Length(min = 7, max = 40)
  @Column(length = 40)
  private String email;

  @Column(name = "ADMIN")
  private boolean administrator;

  @ManyToMany(mappedBy = "subscriber")
  private Collection<Topic> subscriptions = new ArrayList<>();

  @OneToMany(mappedBy = "user")
  private Collection<Status> status = new HashSet<Status>();

  /**
   * JPA-kompatibler Kostruktor. Wird nur von JPA verwendet und darf private sein.
   */
  public User() {
    // JPA benötigt einen Default-Konstruktor!
  }

  /**
   * Konstruktor zum Initialisieren eines neuen Anwenders.
   * 
   * @param login login, mindestens 4 Zeichen lang
   * @param password Passwort inklusive Hash "{noop}"
   * @param administrator Flag (true für Administratorrechte)
   * @param email email, mindestens 7 Zeichen
   */
  public User(String login, String name, String password, boolean administrator, String email) {
    super();
    this.login = login;
    this.name = name;
    this.password = password;
    this.administrator = administrator;
    this.email = email;
  }

  @Override
  public String toString() {
    return login + (administrator ? " (admin)" : "");
  }

  public void addSubscription(Topic topic) {
    subscriptions.add(topic);
  }

  public void removeSubscription(Topic topic) {
    subscriptions.remove(topic);
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getLogin() {
    return login;
  }

  public String getName() {
    return name;
  }

  public String getPassword() {
    return password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public boolean isAdministrator() {
    return administrator;
  }

  public Collection<Topic> getSubscriptions() {
    return subscriptions;
  }

  public Collection<Status> getStatus() {
    return status;
  }

  /*
   * Standard-Methoden. Es ist sinnvoll, hier auf die Auswertung der Assoziationen zu verzichten,
   * nur die Primärschlüssel zu vergleichen und insbesonderen Getter zu verwenden, um auch mit den
   * generierten Hibernate-Proxys kompatibel zu bleiben.
   */

  @Override
  public int hashCode() {
    return Objects.hash(administrator, login, password);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof User)) {
      return false;
    }
    User other = (User) obj;
    return Objects.equals(getLogin(), other.getLogin());
  }

}
