package edu.hm.cs.katz.swt2.agenda.mvc;

import java.util.List;
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
import edu.hm.cs.katz.swt2.agenda.service.TaskService;
import edu.hm.cs.katz.swt2.agenda.service.TopicService;
import edu.hm.cs.katz.swt2.agenda.service.dto.OwnerTopicDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.SubscriberTopicDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.UserDisplayDto;

/**
 * Controller-Klasse für alle Interaktionen, die die Anzeige und Verwaltung von Topics betrifft.
 * Controller reagieren auf Aufrufe von URLs. Sie benennen ein View-Template (Thymeleaf-Vorlage) und
 * stellen Daten zusammen, die darin dargestellt werden. Dafür verwenden Sie Methoden der
 * Service-Schicht.
 * 
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
@Controller
public class TopicController extends AbstractController {

  @Autowired
  private TopicService topicService;

  @Autowired
  private TaskService taskService;

  /**
   * Verarbeitet die Löschung eines Topics.
   */
  @PostMapping("topics/{id}/delete")
  public String handleDeletion(Authentication auth, @PathVariable("id") String id,
      RedirectAttributes redirectAttributes) {
    try {
      topicService.deleteTopic(id, auth.getName());
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:/topics/";
    }
    redirectAttributes.addFlashAttribute("success", "Topic wurde gelöscht!");
    return "redirect:/topics/";
  }

  /**
   * Verarbeitet Aktualisierung eines Topics.
   */
  @PostMapping("topics/{uuid}/manage")
  public String handleUpdate(@ModelAttribute("topic") SubscriberTopicDto topic, Authentication auth,
      @PathVariable("uuid") String uuid, RedirectAttributes redirectAttributes) {

    try {
      topicService.updateTopic(uuid, auth.getName(), topic.getTitle(), topic.getShortDescription(),
          topic.getLongDescription());
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:/topics/{uuid}/manage";
    }
    redirectAttributes.addFlashAttribute("success", "Topic wurde aktualisiert!");
    return "redirect:/topics/{uuid}/manage";
  }

  /**
   * Erstellt die Übersicht über alle Topics des Anwenders, d.h. selbst erzeugte und abonnierte.
   */
  @GetMapping("/topics")
  public String getTopicListView(Model model, Authentication auth,
      @RequestParam(name = "search", required = false, defaultValue = "") String search) {
    model.addAttribute("search", new Search());
    model.addAttribute("managedTopics", topicService.getManagedTopics(auth.getName(), search));
    model.addAttribute("topics", topicService.getSubscriptions(auth.getName(), search));
    model.addAttribute("newTopic", new SubscriberTopicDto(null, null, "", null, null));
    return "topic-listview";
  }

  /**
   * Nimmt den Formularinhalt vom Formular zum Erstellen eines Topics entgegen und legt einen
   * entsprechendes Topic an. Kommt es dabei zu einer Exception, wird das Erzeugungsformular wieder
   * angezeigt und eine Fehlermeldung eingeblendet. Andernfalls wird auf die Übersicht der Topics
   * weitergeleitet und das Anlegen in einer Einblendung bestätigt.
   */
  @PostMapping("/topics")
  public String handleTopicCreation(Model model, Authentication auth,
      @ModelAttribute("newTopic") SubscriberTopicDto topic, RedirectAttributes redirectAttributes) {
    try {
      topicService.createTopic(topic.getTitle(), topic.getShortDescription(),
          topic.getLongDescription(), auth.getName());
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:/topics";
    }
    redirectAttributes.addFlashAttribute("success", "Topic " + topic.getTitle() + " angelegt.");
    return "redirect:/topics";
  }

  /**
   * Erzeugt Anzeige eines Topics mit Informationen für den Ersteller.
   */
  @GetMapping("/topics/{uuid}/manage")
  public String createTopicManagementView(Model model, Authentication auth,
      @PathVariable("uuid") String uuid) {
    OwnerTopicDto topic = topicService.getManagedTopic(uuid, auth.getName());
    model.addAttribute("topic", topic);
    model.addAttribute("tasks", taskService.getManagedTasks(uuid, auth.getName()));
    List<UserDisplayDto> subscribers =
        topicService.getSubscribersWithCompletedTasks(uuid, auth.getName());
    model.addAttribute("subscribers", subscribers);
    return "topic-management";
  }

  /**
   * Nimmt den Key entgegen und erstellt ein Abonnement.
   */
  @PostMapping("/topics/{key}/register")
  public String handleTopicRegistration(@RequestParam("key") String key, Authentication auth,
      RedirectAttributes redirectAttributes) {
    String uuid = "";
    try {
      uuid = topicService.getTopicUuid(key);
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:/topics";
    }
    if (topicService.isSubscriberOfTopic(uuid, auth.getName())) {
      redirectAttributes.addFlashAttribute("error", "Du hast das Topic bereits abonniert!");
      return "redirect:/topics";
    }
    if (topicService.isCreatorOfTopic(uuid, auth.getName())) {
      redirectAttributes.addFlashAttribute("error",
          "Du kannst dein eigenes Topic nicht abonnieren!");
      return "redirect:/topics";
    }
    topicService.subscribe(uuid, auth.getName());
    redirectAttributes.addFlashAttribute("success", "Du hast das Topic erfolgreich abonniert!");
    return "redirect:/topics/" + uuid;
  }

  /**
   * Erstellt Übersicht eines Topics für einen Abonennten.
   */
  @GetMapping("/topics/{uuid}")
  public String createTopicView(Model model, Authentication auth,
      @PathVariable("uuid") String uuid) {
    SubscriberTopicDto topic = topicService.getTopic(uuid, auth.getName());
    model.addAttribute("topic", topic);
    model.addAttribute("tasks", taskService.getTasksForTopic(uuid, auth.getName()));
    return "topic";
  }

  /**
   * Deabonniert ein Topic.
   */
  @PostMapping("/topics/{uuid}/unsubscribe")
  public String handleTopicUnsubscription(Authentication auth, @PathVariable("uuid") String uuid) {
    topicService.unsubscribe(uuid, auth.getName());
    return "redirect:/topics/";
  }

  /**
   * Verarbeitet die Erstellung eines Tasks.
   */
  @PostMapping("/topics/{uuid}/createTask")
  public String handleTaskCreation(Model model, Authentication auth,
      @PathVariable("uuid") String uuid, @RequestParam("title") String title,
      @RequestParam("shortDescription") String shortDescription,
      @RequestParam("longDescription") String longDescription,
      @RequestParam("deadline") String deadline, RedirectAttributes redirectAttributes) {

    try {

      taskService.createTask(uuid, title, shortDescription,
          longDescription, auth.getName(), deadline, true);
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:/topics/" + uuid + "/manage";
    }
    redirectAttributes.addFlashAttribute("success",
        "Task \"" + title + "\" erstellt.");
    return "redirect:/topics/" + uuid + "/manage";
  }
}
