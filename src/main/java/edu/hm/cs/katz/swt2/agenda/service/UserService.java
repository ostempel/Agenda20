package edu.hm.cs.katz.swt2.agenda.service;

import java.util.List;
import edu.hm.cs.katz.swt2.agenda.service.dto.UserDisplayDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.UserRegistrationDto;

/**
 * Serviceklasse für Verarbeitung von Usern.
 * 
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
public interface UserService {
  
  /**
   * Zugriff auf alle User.
   */
  List<UserDisplayDto> getAllUsers();
  
  /**
   * Zugriff auf alle Admins.
   */
  List<UserDisplayDto> findeAdmins();
  
  /**
   * Zugriff auf Informationen eines Users.
   */
  UserDisplayDto getUserInfo(String login);
  
  /**
   * Erstellt einen neuen User.
   */
  void legeAn(String login, String name, String password, boolean isAdmin, String email);

  /**
   * Registriert einen neuen User.
   */
  void registerNewUser(String login, String name, String password, String matchingPassword,
      String email);
  
  /**
   * Zugriff auf alle User.
   */
  List<UserRegistrationDto> getAllRegistrations();
  
  /**
   * Lehnt eine Registrierung ab.
   */
  void denyRegistration(String login);
  
  /**
   * Akzeptiert eine Registrierung.
   */
  void acceptRegistration(String login);
  
  /**
   * Ändert die Informationen eines Users.
   */
  void updateUserinfo(String login, String name, String email);

  /**
   * Ändert das Passwort eines Users.
   */
  void updatePassword(String login, String oldPassword, String newPassword,
      String newPassword2);
}
