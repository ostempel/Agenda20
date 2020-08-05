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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import edu.hm.cs.katz.swt2.agenda.service.TaskService;
import edu.hm.cs.katz.swt2.agenda.service.dto.OwnerTaskDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.StatusDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.SubscriberTaskDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.TaskDto;

@Controller
public class TaskController extends AbstractController {

  @Autowired
  private TaskService taskService;

  /**
   * Verarbeitet die Löschung eines Tasks.
   */
  @PostMapping("tasks/{id}/delete")
  public String handleDeletion(Authentication auth, @PathVariable("id") Long id,
      RedirectAttributes redirectAttributes) {
    TaskDto taskDto = taskService.getManagedTask(id, auth.getName());
    try {
      taskService.deleteTask(id, auth.getName());
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:/topics/" + taskDto.getTopic().getUuid() + "/manage";
    }
    redirectAttributes.addFlashAttribute("success", "Task wurde gelöscht!");
    return "redirect:/topics/" + taskDto.getTopic().getUuid() + "/manage";
  }

  /**
   * Aktualisiert den Task.
   */
  @PostMapping("tasks/{id}/manage")
  public String handleUpdate(@ModelAttribute("task") TaskDto task, @PathVariable("id") Long id,
      Model model, Authentication auth, String shortDescription, String longDescription,
      RedirectAttributes redirectAttributes) {
    try {
      taskService.updateTask(id, auth.getName(), task.getShortDescription(),
          task.getLongDescription(), task.getDeadline());
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:/tasks/" + id + "/manage";
    }
    redirectAttributes.addAttribute("success", "Task wurde aktualisiert!");
    return "redirect:/tasks/" + id + "/manage";
  }

  /**
   * Erstellt die Taskansicht für den Verwalter/Ersteller eines Topics.
   */
  @GetMapping("tasks/{id}/manage")
  public String getManagerTaskView(Model model, Authentication auth, @PathVariable("id") Long id) {
    OwnerTaskDto task = taskService.getManagedTask(id, auth.getName());
    boolean hasComments = false;
    for (StatusDto status : task.getStatuses()) {
      if (!status.getComment().isBlank() || status.getComment() != "") {
        hasComments = true;
      }
    }
    model.addAttribute("hasComments", hasComments);
    model.addAttribute("task", task);
    return "task-management";
  }


  /**
   * Erstellt die Taskansicht für Abonnenten.
   */
  @GetMapping("tasks/{id}")
  public String getSubscriberTaskView(Model model, Authentication auth,
      @PathVariable("id") Long id) {
    TaskDto task = taskService.getTask(id, auth.getName());
    SubscriberTaskDto status = taskService.getTask(id, auth.getName());
    StatusDto comment = taskService.getStatus(id, auth.getName());
    model.addAttribute("task", task);
    model.addAttribute("status", status);
    model.addAttribute("comment", comment);
    return "task";
  }

  /**
   * Fügt einen Kommentar hinzu oder überschreibt den vorhandenen Kommentar.
   */
  @PostMapping("tasks/{id}/commit")
  public String handleComment(@ModelAttribute("comment") StatusDto comment,
      @PathVariable("id") Long id,
      @RequestHeader(value = "referer", required = true) String referer, Model model,
      Authentication auth, RedirectAttributes redirectAttributes) {

    try {
      taskService.commentTask(id, auth.getName(), comment.getComment());
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:" + referer;
    }
    redirectAttributes.addFlashAttribute("success", "Kommentar wurde aktualisiert!");

    return "redirect:" + referer;
  }


  /**
   * Verarbeitet die Markierung eines Tasks als "Done".
   */
  @PostMapping("tasks/{id}/check")
  public String handleTaskChecking(Model model, Authentication auth, @PathVariable("id") Long id,
      @RequestHeader(value = "referer", required = true) String referer) {
    taskService.checkTask(id, auth.getName());
    return "redirect:" + referer;
  }

  /**
   * Setzt den Status eines Tasks zurück.
   */
  @PostMapping("tasks/{id}/reset")
  public String handleTaskReseting(Model model, Authentication auth, @PathVariable("id") Long id,
      @RequestHeader(value = "referer", required = true) String referer) {
    taskService.resetTask(id, auth.getName());
    return "redirect:" + referer;
  }

  /**
   * Erstellt die Übersicht aller Tasks abonnierter Topics für einen Anwender.
   */
  @GetMapping("tasks")
  public String getSubscriberTaskListView(Model model, Authentication auth,
      @ModelAttribute("search") Search searchModel,
      @RequestParam(name = "search", required = false, defaultValue = "") String search,
      @RequestParam(name = "searchType", required = false,
          defaultValue = "") SearchEnum searchType) {
    List<SubscriberTaskDto> tasks =
        taskService.getSubscribedTasks(auth.getName(), search, searchType);
    if (searchModel == null) {
      model.addAttribute("search", new Search());
    }
    model.addAttribute("tasks", tasks);
    return "task-listview";
  }
}