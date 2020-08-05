package edu.hm.cs.katz.swt2.agenda.service;

import java.util.List;
import edu.hm.cs.katz.swt2.agenda.mvc.SearchEnum;
import edu.hm.cs.katz.swt2.agenda.service.dto.OwnerTaskDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.StatusDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.SubscriberTaskDto;

/**
 * Serviceklasse für Verarbeitung von Tasks.
 * 
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
public interface TaskService {

  /**
   * Erstellt einen neuen Task.
   */
  Long createTask(String topicUuid, String title, String shortDescription, String longDescription,
      String login, String deadline, boolean sendEmailToSubscriber);

  /**
   * Zugriff auf einen Task (priviligierte Sicht für Ersteller des Topics).
   */
  OwnerTaskDto getManagedTask(Long taskId, String login);

  /**
   * Zugriff auf alle Tasks eines eigenen Topics.
   */
  List<OwnerTaskDto> getManagedTasks(String topicUuid, String login);

  /**
   * Zugriff auf einen Task (Abonnentensicht).
   */
  SubscriberTaskDto getTask(Long taskId, String login);

  /**
   * Zugriff auf alle Tasks abonnierten Topics.
   */
  List<SubscriberTaskDto> getSubscribedTasks(String login, String search, SearchEnum searchType);

  /**
   * Zugriff auf alle Tasks eines abonnierten Topics.
   */
  List<SubscriberTaskDto> getTasksForTopic(String topicUuid, String login);

  /**
   * Markiert einen Task für einen Abonnenten als "done".
   */
  void checkTask(Long taskId, String login);

  /**
   * Löscht einen Task.
   */
  void deleteTask(Long id, String login);

  /**
   * Aktualisiert einen Task.
   */
  void updateTask(Long taskId, String login, String shortDescription, String longDescription,
      String deadline);

  /**
   * Setzt den Status eines Task zurück.
   */
  void resetTask(Long id, String name);

  /**
   * Methode zum kommentieren eines Tasks.
   */
  void commentTask(Long id, String login, String comment);

  /**
   * Methode zum ausgeben eines Status für einen Task.
   */
  StatusDto getStatus(Long taskId, String login);

}
