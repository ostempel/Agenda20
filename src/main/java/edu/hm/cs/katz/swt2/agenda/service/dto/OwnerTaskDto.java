package edu.hm.cs.katz.swt2.agenda.service.dto;

import java.util.List;

/**
 * Transferobjekt für Tasks mit Metadaten, die nur für Verwalter eines Tasks (d.h. Eigentümer des
 * Topics) sichtbar sind. Transferobjekte sind Schnittstellenobjekte der Geschäftslogik; Sie sind
 * nicht Teil des Modells, so dass Änderungen an den Transferobjekten die Überprüfungen der
 * Geschäftslogik nicht umgehen können.
 * 
 * @see TaskDto
 * 
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
public class OwnerTaskDto extends TaskDto {

  private long countDoneUser;
  private List<StatusDto> statuses;

  /**
   * Konstruktor.
   * @param countDoneUser Anzahl der User, die den Task erledigt haben.
   */
  public OwnerTaskDto(Long id, String title, String shortDescription, String longDescription,
      SubscriberTopicDto topicDto, long countDoneUser, String deadline) {
    super(id, title, shortDescription, longDescription, topicDto, deadline);
    this.countDoneUser = countDoneUser;
  }

  public long getCountDoneUser() {
    return countDoneUser;
  }

  public void setCountDoneUser(long countDoneUser) {
    this.countDoneUser = countDoneUser;
  }

  public List<StatusDto> getStatuses() {
    return statuses;
  }

  public void setStatus(List<StatusDto> statuses) {
    this.statuses = statuses;
  }
}
