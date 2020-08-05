package edu.hm.cs.katz.swt2.agenda.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository zum Zugriff auf gespeicherte Registrierungen. 
 */
@Repository
public interface UserRegistrationRepository extends JpaRepository<UserRegistration, String> {
  
  /**
   * Findet eine Registrierung für eine gegebene email.
   * 
   * @param email
   * @return
   */
  @Query ("select u from UserRegistration u where email = ?1")
  UserRegistration findUserRegistrationWithEmail(String email);
}