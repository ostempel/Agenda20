package edu.hm.cs.katz.swt2.agenda.mvc;

import edu.hm.cs.katz.swt2.agenda.service.UserService;
import edu.hm.cs.katz.swt2.agenda.service.dto.UserDisplayDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.UserManagementDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller-Klasse für alle Interaktionen, die das Verwalten der Anwender betrifft. Controller
 * reagieren auf Aufrufe von URLs. Sie benennen ein View-Template (Thymeleaf-Vorlage) und stellen
 * Daten zusammen, die darin dargestellt werden. Dafür verwenden Sie Methoden der Service-Schicht.
 * 
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
@Controller
public class UserController extends AbstractController {

  @Autowired
  private UserService userService;

  /**
   * Erzeugt eine Listenansicht mit allen Anwendern.
   */
  @GetMapping("/users")
  public String getUserListView(Model model, Authentication auth) {
    model.addAttribute("users", userService.getAllUsers());
    model.addAttribute("newUser", new UserManagementDto());
    return "user-listview";
  }

  /**
   * Erzeugt eine Formularansicht für das Erstellen eines Anwenders.
   */
  @GetMapping("/users/create")
  public String getUserCreationView(Model model) {
    model.addAttribute("newUser", new UserManagementDto());
    return "user-creation";
  }

  /**
   * Nimmt den Formularinhalt vom Formular zum Erstellen eines Anwenders entgegen und legt einen
   * entsprechenden Anwender an. Kommt es dabei zu einer Exception, wird das Erzeugungsformular
   * wieder angezeigt und eine Fehlermeldung eingeblendet. Andernfalls wird auf die Listenansicht
   * der Anwender weitergeleitet und das Anlegen in einer Einblendung bestätigt.
   */
  @PostMapping("users")
  public String handleUserCreation(Model model,
      @ModelAttribute("newUser") UserManagementDto anwender,
      RedirectAttributes redirectAttributes) {
    try {
      userService.legeAn(anwender.getLogin(), anwender.getName(), anwender.getPassword(), false,
          anwender.getEmail());
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:/users/create";
    }
    redirectAttributes.addFlashAttribute("success",
        "Anwender " + anwender.getLogin() + " erstellt.");
    return "redirect:/users";
  }

  /**
   * Erzeugt eine Formularansicht für das Bearbeiten eines Profils.
   */
  @GetMapping("/profile")
  public String getUserProfileView(Model model, Authentication auth) {
    model.addAttribute("user1", userService.getUserInfo(auth.getName()));
    return "profile";
  }

  /**
   * Aktualisiert die Nutzerinformationen.
   */
  @PostMapping("user/updateUserinfo")
  public String handleUserProfileUpdate(Model model, Authentication auth,
      @ModelAttribute("user1") UserDisplayDto user, RedirectAttributes redirectAttributes) {
    try {
      userService.updateUserinfo(auth.getName(), user.getName(), user.getEmail());
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:/profile";
    }
    redirectAttributes.addFlashAttribute("success",
        "Nutzerinformationen erfolgreich aktualisiert.");
    return "redirect:/profile";
  }

  /**
   * Aktualisiert das Passwort.
   */
  @PostMapping("user/updatePassword")
  public String handleUserProfileUpdate(Model model, Authentication auth,
      RedirectAttributes redirectAttributes, @RequestParam("oldPassword") String oldPassword,
      @RequestParam("newPassword") String newPassword,
      @RequestParam("newPassword2") String newPassword2) {
    try {
      userService.updatePassword(auth.getName(), oldPassword, newPassword, newPassword2);
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:/profile";
    }
    redirectAttributes.addFlashAttribute("success", "Passwort erfolgreich aktualisiert.");
    return "redirect:/profile";
  }

  /**
   * Erstellt die Ansicht für alle Regestrierungen.
   */
  @GetMapping("/users/registrations")
  public String getUserRegistrationView(Model model) {
    model.addAttribute("users", userService.getAllRegistrations());
    return "user-registration-listview";
  }

  /**
   * Verarbeitet das Akzeptieren einer neuen Registrierung.
   */
  @PostMapping("/users/accept/{login}")
  public String handleAcceptUserRegistration(Model model, @PathVariable("login") String login,
      RedirectAttributes redirectAttributes) {
    try {
      userService.acceptRegistration(login);
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:/users/registrations";
    }
    redirectAttributes.addFlashAttribute("success", "Der User wurde akzeptiert!");
    return "redirect:/users/registrations";
  }

  /**
   * Verarbeitet das Ablehnen einer neuen Registrierung.
   */
  @PostMapping("/users/deny/{login}")
  public String handleDenyUserRegistration(Model model, @PathVariable("login") String login,
      RedirectAttributes redirectAttributes) {
    try {
      userService.denyRegistration(login);
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:/users/registrations";
    }
    redirectAttributes.addFlashAttribute("success", "Der User wurde abgelehnt!");
    return "redirect:/users/registrations";
  }
}
