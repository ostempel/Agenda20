package edu.hm.cs.katz.swt2.agenda.service;


import edu.hm.cs.katz.swt2.agenda.service.dto.OwnerTopicDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.SubscriberTopicDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.UserDisplayDto;
import java.util.List;

/**
 * Serviceklasse für Verarbeitung von Topics.
 * 
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
public interface TopicService {

  /**
   * Erstellt ein neues Topic.
   */
  String createTopic(String title, String shortDescription, String longDescription, String login);

  /**
   * Zugriff auf ein eigenes Topic.
   */
  OwnerTopicDto getManagedTopic(String topicUuid, String login);

  /**
   * Zugriff auf alle eigenen Topics.
   */
  List<OwnerTopicDto> getManagedTopics(String login, String search);

  /**
   * Zugriff auf ein abonniertes Topic.
   */
  SubscriberTopicDto getTopic(String topicUuid, String login);

  /**
   * Zugriff auf alle abonnierten Topics.
   */
  List<SubscriberTopicDto> getSubscriptions(String login, String search);

  /**
   * Abonnieren eines Topics.
   */
  void subscribe(String topicUuid, String login);

  /**
   * Deabonnieren eines Topics.
   */
  void unsubscribe(String topicUuid, String login);

  /**
   * Löscht ein Topic.
   */
  void deleteTopic(String uuid, String login);

  /**
   * Updated ein Topic.
   */
  void updateTopic(String uuid, String login, String title, String shortDescription,
      String longDescription);

  /**
   * Gibt das Topic zu einem Schlüssel zurück.
   */
  String getTopicUuid(String key);

  /**
   * Überprüft ob ein User Abonnent eines Topics ist.
   */
  boolean isSubscriberOfTopic(String uuid, String login);

  /**
   * Überprüft ob ein User Ersteller eines Topics ist.
   */
  boolean isCreatorOfTopic(String uuid, String login);

  /**
   * Gibt alle Abonnenten eines Topics inklusive Anzahl abgeschlossener Tasks im Topic zurück.
   */
  List<UserDisplayDto> getSubscribersWithCompletedTasks(String uuid, String name);
}
