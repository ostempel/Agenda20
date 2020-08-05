package edu.hm.cs.katz.swt2.agenda.service.dto;

/**
 * Transferobjekt für einfache Anzeigeinformationen von Topics. Transferobjekte sind
 * Schnittstellenobjekte der Geschäftslogik; Sie sind nicht Teil des Modells, so dass Änderungen an
 * den Transferobjekten die Überprüfungen der Geschäftslogik nicht umgehen können.
 * 
 * @see OwnerTopicDto
 * 
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
public class SubscriberTopicDto extends TopicDto {

  private long countNotDoneTasks;

  /**
   * Konstruktor.
   * 
   * @param uuid
   * @param creator
   * @param title
   * @param shortDescription
   * @param longDescription
   */
  public SubscriberTopicDto(String uuid, UserDisplayDto creator, String title,
      String shortDescription, String longDescription) {
    super(uuid, creator, title, shortDescription, longDescription);
    countNotDoneTasks = 0;
  }

  public long getCountNotDoneTasks() {
    return countNotDoneTasks;
  }

  public void setCountNotDoneTasks(long countNotDoneTasks) {
    this.countNotDoneTasks = countNotDoneTasks;
  }
}
