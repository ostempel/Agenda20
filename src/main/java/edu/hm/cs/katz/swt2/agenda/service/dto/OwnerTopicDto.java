package edu.hm.cs.katz.swt2.agenda.service.dto;

import java.util.ArrayList;
import java.util.Collection;
import edu.hm.cs.katz.swt2.agenda.persistence.User;

/**
 * Transferobjekt für Topics mit Metadaten, die nur für Verwalter eines Topics (d.h. Eigentümer des
 * Topics) sichtbar sind. Transferobjekte sind Schnittstellenobjekte der Geschäftslogik; Sie sind
 * nicht Teil des Modells, so dass Änderungen an den Transferobjekten die Überprüfungen der
 * Geschäftslogik nicht umgehen können.
 * 
 * @see TopicDto
 * 
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
public class OwnerTopicDto extends TopicDto {

  private boolean isSubscribed;
  private Collection<User> subscriber = new ArrayList<User>();
  private long countTasks;

  /**
   * Konstruktor. 
   * 
   * @param uuid UUID
   * @param user User
   * @param title Titel
   * @param shortDescription Kurzbeschreibung
   * @param longDescription Langbeschreibung
   * @param isSubscribed ist das Topic abonniert?
   * @param subscriber Abonnenten
   * @param countTasks Anzahl Tasks
   */
  public OwnerTopicDto(String uuid, UserDisplayDto user, String title, String shortDescription,
      String longDescription, boolean isSubscribed, Collection<User> subscriber, long countTasks) {
    super(uuid, user, title, shortDescription, longDescription);
    
    this.isSubscribed = isSubscribed;
    this.subscriber = subscriber;
    this.countTasks = countTasks;
  }

  public boolean getIsSubscribed() {
    return isSubscribed;
  }

  public void setIsSubscribed(boolean isSubscribed) {
    this.isSubscribed = isSubscribed;
  }
  
  public String getKey() {
    return getUuid().substring(28);
  }

  public Collection<User> getSubscriber() {
    return subscriber;
  }

  public void setSubscriber(Collection<User> subscriber) {
    this.subscriber = subscriber;
  }

  public long getCountTasks() {
    return countTasks;
  }

  public void setCountTasks(long countTasks) {
    this.countTasks = countTasks;
  }
}
