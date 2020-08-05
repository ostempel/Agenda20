package edu.hm.cs.katz.swt2.agenda.service.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class UserRegistrationDto {
  @NotNull
  @NotEmpty
  private String login;
   
  @NotNull
  @NotEmpty
  private String name;
   
  @NotNull
  @NotEmpty
  private String password;
  private String matchingPassword;
   
  @NotNull
  @NotEmpty
  private String email;

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getMatchingPassword() {
    return matchingPassword;
  }

  public void setMatchingPassword(String matchingPassword) {
    this.matchingPassword = matchingPassword;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}