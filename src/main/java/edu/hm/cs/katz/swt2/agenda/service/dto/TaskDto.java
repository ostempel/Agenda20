package edu.hm.cs.katz.swt2.agenda.service.dto;

/**
 * Transferobjekt für einfache Anzeigeinformationen von Tasks. Transferobjekte sind
 * Schnittstellenobjekte der Geschäftslogik; Sie sind nicht Teil des Modells, so dass Änderungen an
 * den Transferobjekten die Überprüfungen der Geschäftslogik nicht umgehen können.
 * 
 * @see OwnerTaskDto
 * @see SubscriberTaskDto
 * 
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
public class TaskDto {

  private Long id;
  private String title;
  private TopicDto topic;
  private String shortDescription;
  private String longDescription;
  private String deadline;

  /**
   * Konstruktor.
   * 
   * @param id
   * @param title
   * @param shortDescription
   * @param longDescription
   * @param topicDto
   * @param deadline
   */
  public TaskDto(Long id, String title, String shortDescription, String longDescription,
      TopicDto topicDto, String deadline) {
    this.id = id;
    this.title = title;
    this.shortDescription = shortDescription;
    this.longDescription = longDescription;
    this.topic = topicDto;
    this.deadline = deadline;
  }

  public String getDeadline() {
    return deadline;
  }

  public void setDeadline(String deadline) {
    this.deadline = deadline;
  }

  public Long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public TopicDto getTopic() {
    return topic;
  }

  public void setTopic(SubscriberTopicDto topic) {
    this.topic = topic;
  }

  public String getShortDescription() {
    return shortDescription;
  }

  public void setShortDescription(String shortDescription) {
    this.shortDescription = shortDescription;
  }

  public String getLongDescription() {
    return longDescription;
  }

  public void setLongDescription(String longDescription) {
    this.longDescription = longDescription;
  }
}
