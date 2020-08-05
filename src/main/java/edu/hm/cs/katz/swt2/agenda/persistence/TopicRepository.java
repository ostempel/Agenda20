package edu.hm.cs.katz.swt2.agenda.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository zum Zugriff auf gespeicherte Topics. Repostory-Interfaces erben eine unglaubliche
 * Menge hilfreicher Methoden. Weitere Methoden kann man einfach durch Benennung definierern. Spring
 * Data ergänzt die Implementierungen zur Laufzeit.
 * 
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
@Repository
public interface TopicRepository extends JpaRepository<Topic, String> {

  /**
   * Findet alle Topics zu einem gegebenen Anwender.
   * 
   * @param creator Anwender
   * @return
   */
  List<Topic> findByCreator(User creator);

  /**
   * Findet alle erstellten Topics und sortiert diese nach Titel in absteigender Reihenfolge.
   * 
   * @param creator Ersteller
   * @return
   */
  List<Topic> findByCreatorOrderByTitleAsc(User creator);

  /**
   * Findet alle abbonierten Topics eines Users und sortiert diese nach Titel in absteigender
   * Reihenfolge.
   * 
   * @param subscriber Abonnent
   * @return
   */
  List<Topic> findBySubscriberOrderByTitleAsc(User subscriber);

  /**
   * Zählt wie viel Topics ein Anwender erstellt hat.
   * 
   * @param user
   * @return
   */
  int countByCreator(User user);

  /**
   * Findet ein Topic dessen UUID auf den gegebenen Schlüssel endet.
   * 
   * @param key
   * @return
   */
  Topic findByUuidEndingWith(String key);
  
  /**
   * Findet alle selbsterstellten Task für ein gegebenes Topic.
   * 
   * @param uuid
   * @return
   */
  @Query("Select t from Task t, Topic o where t.topic = o.uuid and o.uuid = ?1 order by t.title asc")
  List<Task> findManagedTaskForTopic(String uuid);
}
