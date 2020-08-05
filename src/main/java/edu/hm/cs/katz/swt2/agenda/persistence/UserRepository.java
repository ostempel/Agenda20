package edu.hm.cs.katz.swt2.agenda.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository zum Zugriff auf gespeicherte Anwenderdaten. Repostory-Interfaces erben eine
 * unglaubliche Menge hilfreicher Methoden. Weitere Methoden kann man einfach durch Benennung
 * definierern. Spring Data ergänzt die Implementierungen zur Laufzeit.
 * 
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

  /**
   * Ermittelt alle Anwender, die Administrator sind (oder alle anderen).
   * 
   * @param isAdministrator Flag zum Filtern
   * @return
   */
  List<User> findByAdministrator(boolean isAdministrator);
  
  /**
   * Findet alle User, die kein Administrator sind.
   * 
   * @return
   */
  List<User> findByAdministratorFalseOrderByLoginAsc();
  
  /**
   * Findet einen User für eine gegebene email.
   * 
   * @param email
   * @return
   */
  @Query ("select u from User u where email = ?1")
  User findUserWithEmail(String email);
}
