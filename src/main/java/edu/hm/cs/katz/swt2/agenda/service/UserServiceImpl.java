package edu.hm.cs.katz.swt2.agenda.service;

import static edu.hm.cs.katz.swt2.agenda.common.SecurityHelper.ADMIN_ROLES;
import static edu.hm.cs.katz.swt2.agenda.common.SecurityHelper.STANDARD_ROLES;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import edu.hm.cs.katz.swt2.agenda.persistence.User;
import edu.hm.cs.katz.swt2.agenda.persistence.UserRegistration;
import edu.hm.cs.katz.swt2.agenda.persistence.UserRegistrationRepository;
import edu.hm.cs.katz.swt2.agenda.persistence.UserRepository;
import edu.hm.cs.katz.swt2.agenda.service.dto.UserDisplayDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.UserRegistrationDto;
import edu.hm.cs.katz.swt2.agenda.service.mail.MailServiceImpl;

/**
 * Service-Klasse zur Verwaltung von Anwendern. Wird auch genutzt, um Logins zu validieren.
 * Servicemethoden sind transaktional und rollen alle Änderungen zurück, wenn eine Exception
 * auftritt. Service-Methoden sollten
 * <ul>
 * <li>keine Modell-Objekte herausreichen, um Veränderungen des Modells außerhalb des
 * transaktionalen Kontextes zu verhindern - Schnittstellenobjekte sind die DTOs (Data Transfer
 * Objects).
 * <li>die Berechtigungen überprüfen, d.h. sich nicht darauf verlassen, dass die Zugriffen über die
 * Webcontroller zulässig sind.</li>
 * </ul>
 * 
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl implements UserDetailsService, UserService {

  private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

  @Autowired
  private UserRepository anwenderRepository;

  @Autowired
  private UserRegistrationRepository userRegistrationRepository;

  @Autowired
  private DtoMapper mapper;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    LOG.info("Gebe Daten zu User {} aus.", username);

    Optional<User> findeMitspieler = anwenderRepository.findById(username);
    if (findeMitspieler.isPresent()) {
      User user = findeMitspieler.get();
      return new org.springframework.security.core.userdetails.User(user.getLogin(),
          user.getPassword(), user.isAdministrator() ? ADMIN_ROLES : STANDARD_ROLES);
    } else {
      LOG.debug("User {} konnte nicht gefunden werden.", username);
      throw new UsernameNotFoundException("Anwender konnte nicht gefunden werden.");
    }
  }

  @Override
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public List<UserDisplayDto> getAllUsers() {
    LOG.info("Gebe alle User aus.");

    List<UserDisplayDto> result = new ArrayList<>();
    for (User anwender : anwenderRepository.findByAdministratorFalseOrderByLoginAsc()) {
      result.add(mapper.createDto(anwender));
    }
    return result;
  }

  @Override
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public List<UserDisplayDto> findeAdmins() {
    LOG.info("Gebe alle Admins aus.");

    // Das Mapping auf DTOs geht eleganter, ist dann aber schwer verständlich.
    List<UserDisplayDto> result = new ArrayList<>();
    for (User anwender : anwenderRepository.findByAdministrator(true)) {
      result.add(mapper.createDto(anwender));
    }
    return result;
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public UserDisplayDto getUserInfo(String login) {
    LOG.info("Lese Daten für User {}.", login);

    User anwender = anwenderRepository.getOne(login);
    return mapper.createDto(anwender);
  }

  @Override
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public void legeAn(String login, String name, String password, boolean isAdministrator,
      String email) {
    LOG.info("Erstelle User {}.", login);
    LOG.debug("Erstelle User {} mit Parameter {}, {}, {}.", login, name, isAdministrator, email);

    validateUser(login, name, password, email);

    // Passwörter müssen Hashverfahren benennen.
    // Wir hashen nicht (noop), d.h. wir haben die
    // Passwörter im Klartext in der Datenbank (böse)
    User anwender = new User(login, name, "{noop}" + password, isAdministrator, email);
    anwenderRepository.save(anwender);
  }

  @Override
  @PreAuthorize("#login==authentication.name OR hasRole('ROLE_ADMIN')")
  public void updateUserinfo(String login, String name, String email) {
    LOG.info("User {} ändet seinen Anzeigenamen und E-Mail.", login);
    LOG.debug("User {} ändert Anzeigename zu {} und E-Mail zu {}.", login, name, email);

    User user = anwenderRepository.getOne(login);

    if (name.length() < 4 || name.length() > 32) {
      LOG.debug("Anzeigename '{}' muss 4-20 Zeichen lang sein.", name);
      throw new ValidationException("Der Anzeigename muss 4-20 Zeichen lang sein!");
    }

    validateEmail(login, email);

    if (!user.getEmail().equals(email)) {
      if (anwenderRepository.findUserWithEmail(email) != null) {
        LOG.debug("Die E-mail {} wird schon für einen User benutzt!", email);
        throw new ValidationException("Die Email wird schon für einen User benutzt!");
      }

      if (userRegistrationRepository.findUserRegistrationWithEmail(email) != null) {
        LOG.debug("Die E-mail {} wird schon für eine Registrierung benutzt!", email);
        throw new ValidationException("Die Email wird schon für eine Registrierung benutzt!");
      }
    }

    user.setName(name);
    user.setEmail(email);
  }

  @Override
  @PreAuthorize("#login==authentication.name OR hasRole('ROLE_ADMIN')")
  public void updatePassword(String login, String oldPassword, String newPassword,
      String newPassword2) {
    LOG.info("User {} ändert Passwort.", login);
    LOG.debug("User {} ändert Passwort.", login);

    String userPassword = anwenderRepository.getOne(login).getPassword();

    if (!userPassword.equals("{noop}" + oldPassword)) {
      LOG.debug("Altes Passwort falsch eingegeben.");
      throw new ValidationException("Altes Passwort falsch!");
    }

    if (!newPassword.equals(newPassword2)) {
      LOG.debug("Das Passwort von User {} stimmt nicht überein.", login);
      throw new ValidationException("Das Passwort stimmt nicht überein!");
    }

    if (userPassword.equals("{noop}" + newPassword)) {
      LOG.debug("Das neue Passwort von User {} unterscheidet sich nicht von dem alten.", login);
      throw new ValidationException("Das neue Passwort darf nicht das alte Passwort sein!");
    }

    Pattern lowerLetter = Pattern.compile("[a-z]");
    Pattern upperLetter = Pattern.compile("[A-Z]");
    Pattern digit = Pattern.compile("[0-9]");
    Pattern special = Pattern.compile("[.!@#$%&*()_+=|<>?{}\\[\\]~-]");

    if (newPassword.length() < 8 || newPassword.length() > 20) {
      LOG.debug("Das Passowort von User {} muss zwischen 8-20 Zeichen lang sein.", login);
      throw new ValidationException("Das Passwort muss 8-20 Zeichen lang sein!");
    }
    if (!(digit.matcher(newPassword).find() && special.matcher(newPassword).find()
        && upperLetter.matcher(newPassword).find() && lowerLetter.matcher(newPassword).find())) {
      LOG.debug("Das Passowort von User {} muss mindestens einen Großbuchstaben, "
          + "einen Kleinbuchstaben, eine Zahl und ein Sonderzeichen enthalten!", login);
      throw new ValidationException(
          "Das Passwort muss mindestens einen Großbuchstaben, einen Kleinbuchstaben, "
              + "eine Zahl und ein Sonderzeichen enthalten!");
    }
    if (newPassword.contains(" ") || newPassword.contains("\t")) {
      LOG.debug("Das Passwort von User {} darf keine " + "Leerzeichen oder Tabulatoren enthalten!",
          login);
      throw new ValidationException(
          "Das Passwort darf keine Leerzeichen oder Tabulatoren enthalten!");
    }

    User user = anwenderRepository.getOne(login);
    user.setPassword("{noop}" + newPassword);
  }

  /**
   * Neuen User registrieren.
   */
  public void registerNewUser(String login, String name, String password, String matchingPassword,
      String email) {
    LOG.info("Erstelle Registration für {}.", login);
    LOG.debug("Erstelle User {} mit Parameter {}, {}.", login, name, email);

    validateUser(login, name, matchingPassword, email);

    if (!(password.equals(matchingPassword))) {
      LOG.debug("Das Passwort von User {} stimmt nicht überein.", login);
      throw new ValidationException("Das Passwort stimmt nicht überein!");
    }

    UserRegistration userRegistration =
        new UserRegistration(login, name, "{noop}" + password, email);
    userRegistrationRepository.save(userRegistration);
  }

  @Override
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public void acceptRegistration(String login) {
    LOG.info("Akzeptiert Registration {}.", login);
    LOG.debug("Akzeptiert Registration {}.", login);

    UserRegistration userRegistration = userRegistrationRepository.getOne(login);

    User anwender = new User(login, userRegistration.getName(), userRegistration.getPassword(),
        false, userRegistration.getEmail());
    anwenderRepository.save(anwender);

    userRegistrationRepository.deleteById(login);

    MailServiceImpl.sendMail(anwender.getEmail(), "Agenda-Registrierung", "Lieber "
        + anwender.getName()
        + ",\n\ndeine Registrierung wurde akzeptiert. Du kannst dich nun auf unserer Taskmanagement-Plattform anmelden!\n\nAgenda");
  }

  @Override
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public void denyRegistration(String login) {
    LOG.info("Lehnt Registration {} ab.", login);
    LOG.debug("Lehnt Registration {} ab.", login);
    
    UserRegistration userRegistration = userRegistrationRepository.getOne(login);
    userRegistrationRepository.deleteById(login);
    
    MailServiceImpl.sendMail(userRegistration.getEmail(), "Agenda-Registrierung", "Lieber "
        + userRegistration.getName()
        + ",\n\ndeine Registrierung wurde abgelehnt.\n\nAgenda");
  }

  @Override
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public List<UserRegistrationDto> getAllRegistrations() {
    LOG.info("Gebe alle Registration aus.");

    List<UserRegistrationDto> result = new ArrayList<>();
    for (UserRegistration user : userRegistrationRepository.findAll()) {
      result.add(mapper.createDto(user));
    }
    return result;
  }

  /**
   * Validiert alle Informationen eines Users.
   */
  private void validateUser(String login, String name, String password, String email) {

    if (anwenderRepository.existsById(login)) {
      LOG.debug("User {} existiert bereits und kann nicht angelegt werden.", login);
      throw new ValidationException("Dieser Anwender existiert bereits!");
    }

    if (userRegistrationRepository.existsById(login)) {
      LOG.debug("User {} hat bereits ein Registrierung angefordert.", login);
      throw new ValidationException("Dieser Login wird schon benutzt!");
    }

    if (login.length() < 4 || login.length() > 20) {
      LOG.debug(
          "Login '{}' muss 4-20 Zeichen lang sein " + "und kann deshalb nicht angelegt werden.",
          login);
      throw new ValidationException("Der Login muss 4-20 Zeichen lang sein!");
    }

    if (name.length() < 4 || name.length() > 32) {
      LOG.debug(
          "Username '{}' muss 4-20 Zeichen lang sein " + "und kann deshalb nicht angelegt werden.",
          name);
      throw new ValidationException("Der Username muss 4-20 Zeichen lang sein!");
    }
    Pattern lowerLetter = Pattern.compile("[a-z]");
    Pattern upperLetter = Pattern.compile("[A-Z]");
    Pattern digit = Pattern.compile("[0-9]");
    Pattern special = Pattern.compile("[.!@#$%&*()_+=|<>?{}\\[\\]~-]");

    if (digit.matcher(login).find() || special.matcher(login).find()
        || upperLetter.matcher(login).find()) {
      LOG.debug("Login '{}' enthält Großbuchstaben oder Sonderzeichen "
          + "und kann deshalb nicht angelegt werden.", login);
      throw new ValidationException(
          "Der Login darf nur aus Kleinbuchstaben bestehen und keine Sonderzeichen enthalten!");
    }

    if (password.length() < 8 || password.length() > 20) {
      LOG.debug("Das Passowort von User {} muss zwischen 8-20 Zeichen lang sein.", login);
      throw new ValidationException("Das Passwort muss 8-20 Zeichen lang sein!");
    }
    if (!(digit.matcher(password).find() && special.matcher(password).find()
        && upperLetter.matcher(password).find() && lowerLetter.matcher(password).find())) {
      LOG.debug("Das Passowort von User {} muss mindestens ein Großbuchstaben, "
          + "ein Kleinbuchstaben, eine Zahl und ein Sonderzeichen enthalten!", login);
      throw new ValidationException(
          "Das Passwort muss mindestens ein Großbuchstaben, ein Kleinbuchstaben, "
              + "eine Zahl und ein Sonderzeichen enthalten!");
    }
    if (password.contains(" ") || password.contains("\t")) {
      LOG.debug("Das Passwort von User {} darf keine " + "Leerzeichen oder Tabulatoren enthalten!",
          login);
      throw new ValidationException(
          "Das Passwort darf keine Leerzeichen oder Tabulatoren enthalten!");
    }

    validateEmail(login, email);

    if (anwenderRepository.findUserWithEmail(email) != null) {
      LOG.debug("Die E-mail {} wird schon für einen User benutzt!", email);
      throw new ValidationException("Die Email wird schon für einen User benutzt!");
    }

    if (userRegistrationRepository.findUserRegistrationWithEmail(email) != null) {
      LOG.debug("Die E-mail {} wird schon für eine Registrierung benutzt!", email);
      throw new ValidationException("Die Email wird schon für eine Registrierung benutzt!");
    }
  }

  /**
   * Prüft E-Mail Adresse auf Korrektheit.
   */
  private void validateEmail(String login, String email) {
    String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(email);
    if (email.length() < 7 || email.length() > 40) {
      LOG.debug("Die E-mail {} von User {} ist zu lang oder kurz!", email, login);
      throw new ValidationException("Die Email muss zwischen 7 und 40 Zeichen lang sein!");
    }
    if (!matcher.matches()) {
      LOG.debug("Die E-mail {} von User {} ist ungültig!", email, login);
      throw new ValidationException("Die Email ist ungültig!");
    }
  }
}
