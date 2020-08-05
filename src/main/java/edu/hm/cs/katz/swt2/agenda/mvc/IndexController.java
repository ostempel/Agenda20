package edu.hm.cs.katz.swt2.agenda.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import edu.hm.cs.katz.swt2.agenda.service.TopicService;
import edu.hm.cs.katz.swt2.agenda.service.UserService;
import edu.hm.cs.katz.swt2.agenda.service.dto.UserRegistrationDto;


/**
 * Controller-Klasse für die Landing-Page. Controller reagieren auf Aufrufe von URLs. Sie benennen
 * ein View-Template (Thymeleaf-Vorlage) und stellen Daten zusammen, die darin dargestellt werden.
 * Dafür verwenden Sie Methoden der Service-Schicht.
 * 
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */

@Controller
public class IndexController extends AbstractController {

  @Autowired
  TopicService topicService;

  @Autowired
  UserService userService;

  /**
   * index wird direkt auf /tasks umgeleitet.
   */
  @GetMapping("/")
  public String getIndexView(@RequestParam(name = "error", required = false) String error,
      Model model) {
    if (error != null) {
      model.addAttribute("error", "Logindaten inkorrekt.");
    }

    return "index";

  }

  /**
   * Erstellt die Login-Ansicht.
   */
  @GetMapping("/login")
  public String getLoginView(Model model,
      @RequestParam(name = "error", required = false) String error) {
    if (error != null) {
      model.addAttribute("error", "Logindaten inkorrekt.");
    }

    return "login";
  }

  /**
   * Erstellt die Ansicht für ein neue Regestrierung.
   */
  @GetMapping("/registration")
  public String getUserRegistration(
      @ModelAttribute("userRegistration") UserRegistrationDto registration, Model model) {
    if (registration != null) {
      model.addAttribute("userRegistration", registration);
    } else {
      UserRegistrationDto user = new UserRegistrationDto();
      model.addAttribute("userRegistration", user);
    }
    return "registration";
  }

  /**
   * Verarbeitung einer Registrierung.
   */
  @PostMapping("/registration")
  public String handleUserRegistration(
      @ModelAttribute("userRegistration") UserRegistrationDto registration,
      RedirectAttributes redirectAttributes) {
    try {
      userService.registerNewUser(registration.getLogin(), registration.getName(),
          registration.getPassword(), registration.getMatchingPassword(), registration.getEmail());
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      redirectAttributes.addFlashAttribute("userRegistration", registration);
      return "redirect:/registration";
    }
    redirectAttributes.addFlashAttribute("success",
        "Du hast dich erfolgreich zur Registrierung angemeldet! Warte bis ein Admin dich freischaltet.");
    return "complete-registration";
  }
}
