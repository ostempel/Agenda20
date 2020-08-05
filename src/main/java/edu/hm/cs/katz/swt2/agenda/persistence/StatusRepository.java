package edu.hm.cs.katz.swt2.agenda.persistence;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import edu.hm.cs.katz.swt2.agenda.common.StatusEnum;

/**
 * Repository zum Zugriff auf gespeicherte Statusinformationen. Repostory-Interfaces erben eine
 * unglaubliche Menge hilfreicher Methoden. Weitere Methoden kann man einfach durch Benennung
 * definierern. Spring Data ergänzt die Implementierungen zur Laufzeit.
 * 
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
@Repository
public interface StatusRepository extends JpaRepository<Status, Long> {

  /**
   * Findet den Status für einen gegebenen Task und einen gegebenen Anwender.
   * 
   * @param user Anwender
   * @param task Task
   * @return Status, <code>null</code>, wenn noch kein Status existiert.
   */
  Status findByUserAndTask(User user, Task task);

  /**
   * Findet den Status für einen gegebenen User und Status-Zustand.
   * 
   * @param user
   * @param status
   * @return
   */
  List<Task> findByUserAndStatus(User user, StatusEnum status);

  /**
   * Findet alle Status für einen gegebenen Task.
   * 
   * @param task
   * @return
   */
  List<Status> findByTask(Task task);

  /**
   * Findet alle Status für einen User und den gesuchten Zuständen/Status und der gewählten Topics
   * und sortiert nach dem zugehörigen Tasktitel.
   * 
   * @param user
   * @param status
   * @param uuids
   * @return
   */
  @Query("select s from Status s, Task t, Topic o where s.task.id = t.id and t.topic = o.uuid and s.user = ?1 and s.status in ?2 and o.uuid in ?3 order by t.title asc")
  List<Status> findByUserAndStatusAndTopicOrderByTitle(User user, Collection<StatusEnum> status,
      Collection<String> uuids);

  /**
   * Zählt wie viele User den gegebenen Status-Zustand bei einem Task haben.
   * 
   * @param status
   * @param taskId
   * @return
   */
  @Query("select count(s) from Status s, Task t where s.task.id = t.id and s.status = ?1 and t.id = ?2")
  long countByStatusFindByTaskWhereStatusIsFertig(StatusEnum status, long taskId);

  /**
   * Zählt wie viele Tasks eines Users zu einem Topic sich in den gegebenen Zuständen befinden.
   * 
   * @param status
   * @param uuid
   * @param user
   * @return
   */
  @Query("select count(s) from Status s, Task t, Topic o where s.task.id = t.id and t.topic = o.uuid and s.status in ?1 and o.uuid = ?2 and s.user = ?3")
  int countByStatusFindByTopicAndUserWhereStatusIn(Collection<StatusEnum> status, String uuid,
      User user);
}
