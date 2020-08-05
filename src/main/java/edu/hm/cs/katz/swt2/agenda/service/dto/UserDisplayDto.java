package edu.hm.cs.katz.swt2.agenda.service.dto;

/**
 * Transferobjekt für einfache Anzeigeinformationen von Anwendern. Transferobjekte sind
 * Schnittstellenobjekte der Geschäftslogik; Sie sind nicht Teil des Modells, so dass Änderungen an
 * den Transferobjekten die Überprüfungen der Geschäftslogik nicht umgehen können.
 * 
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
public class UserDisplayDto {

  private String login = "";
  private String name = "";
  private String email = "";
  private int topicCount = 0;
  private int subscriptionCount = 0;
  private int completedTasks;
  private int allTasks;
  private int openTasks;
  
  public String getDisplayName() {
    return name;
  }

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
  
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public int getTopicCount() {
    return topicCount;
  }

  public void setTopicCount(int topicCount) {
    this.topicCount = topicCount;
  }

  public int getSubscriptionCount() {
    return subscriptionCount;
  }

  public void setSubscriptionCount(int subscriptionCount) {
    this.subscriptionCount = subscriptionCount;
  }

  public long getCompletedTasks() {
    return completedTasks;
  }

  public void setCompletedTasks(int completedTasks) {
    this.completedTasks = completedTasks;
  }

  public int getAllTasks() {
    return allTasks;
  }

  public void setAllTasks(int allTasks) {
    this.allTasks = allTasks;
  }

  public int getOpenTasks() {
    return openTasks;
  }

  public void setOpenTasks(int openTasks) {
    this.openTasks = openTasks;
  }
}
