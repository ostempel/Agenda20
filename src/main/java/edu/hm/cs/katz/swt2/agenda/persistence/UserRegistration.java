package edu.hm.cs.katz.swt2.agenda.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

/**
 * Modellklasse für die Speicherungen von Registrierungen.
 * Sobald Registrierungs akzeptiiert wird, wird die Registrierung in einen neuen User umgewandelt.
 */
@Entity
public class UserRegistration {
  
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
  
  /**
   * JPA-kompatibler Kostruktor. Wird nur von JPA verwendet und darf private sein.
   */
  public UserRegistration() {
    // JPA benötigt einen Default-Konstruktor!
  }

  /**
   * Konstruktor zum Initialisieren eines neuen Anwenders.
   * 
   * @param login login, mindestens 4 Zeichen lang
   * @param password Passwort inklusive Hash "{noop}"
   * @param email email, mindestens 7 Zeichen lang
   */
  public UserRegistration(String login, String name, String password, String email) {
    super();
    this.login = login;
    this.name = name;
    this.password = password;
    this.email = email;
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
}