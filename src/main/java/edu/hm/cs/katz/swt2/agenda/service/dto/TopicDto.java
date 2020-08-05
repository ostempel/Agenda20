package edu.hm.cs.katz.swt2.agenda.service.dto;

public class TopicDto {
  
  private String uuid;
  private UserDisplayDto creator;
  private String title;
  private String shortDescription;
  private String longDescription;

  /**
   * Konstruktor.
   */
  public TopicDto(String uuid, UserDisplayDto creator, String title,
      String shortDescription, String longDescription) {
    this.uuid = uuid;
    this.creator = creator;
    this.title = title;
    this.shortDescription = shortDescription;
    this.longDescription = longDescription;
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

  public String getUuid() {
    return uuid;
  }

  public UserDisplayDto getCreator() {
    return creator;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
}
