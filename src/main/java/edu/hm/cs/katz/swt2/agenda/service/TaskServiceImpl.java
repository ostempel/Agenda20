package edu.hm.cs.katz.swt2.agenda.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javax.validation.ValidationException;
import org.apache.commons.collections4.SetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import edu.hm.cs.katz.swt2.agenda.common.StatusEnum;
import edu.hm.cs.katz.swt2.agenda.mvc.SearchEnum;
import edu.hm.cs.katz.swt2.agenda.persistence.Status;
import edu.hm.cs.katz.swt2.agenda.persistence.StatusRepository;
import edu.hm.cs.katz.swt2.agenda.persistence.Task;
import edu.hm.cs.katz.swt2.agenda.persistence.TaskRepository;
import edu.hm.cs.katz.swt2.agenda.persistence.Topic;
import edu.hm.cs.katz.swt2.agenda.persistence.TopicRepository;
import edu.hm.cs.katz.swt2.agenda.persistence.User;
import edu.hm.cs.katz.swt2.agenda.persistence.UserRepository;
import edu.hm.cs.katz.swt2.agenda.service.dto.OwnerTaskDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.StatusDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.SubscriberTaskDto;
import edu.hm.cs.katz.swt2.agenda.service.mail.MailServiceImpl;

@Component
@Transactional(rollbackFor = Exception.class)
public class TaskServiceImpl implements TaskService {

  private static final Logger LOG = LoggerFactory.getLogger(TaskServiceImpl.class);

  @Autowired
  private TaskRepository taskRepository;

  @Autowired
  private TopicRepository topicRepository;

  @Autowired
  private UserRepository anwenderRepository;

  @Autowired
  private StatusRepository statusRepository;

  @Autowired
  private DtoMapper mapper;

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public void deleteTask(Long id, String login) {
    LOG.info("User {} löscht Task.", login);
    LOG.debug("User {} löscht Task {}.", login, id);

    Task t = taskRepository.getOne(id);
    Topic topic = t.getTopic();
    User user = anwenderRepository.getOne(login);

    if (!user.equals(topic.getCreator())) {
      LOG.warn("User {} ist nicht berechtigt auf das Topic {} zuzugreifen.", login,
          topic.getTitle());
      throw new AccessDeniedException("Kein Zugriff auf das Topic!");
    }

    taskRepository.delete(t);
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public Long createTask(String uuid, String title, String shortDescription, String longDescription,
      String login, String deadline, boolean sendEmail) {
    LOG.info("User {} erstellt Task.", login);
    LOG.debug(
        "User {} erstellt Task {} in Topic mit uuid {} und "
            + "Kurzbeschreibung {} und Langbeschreibung {} und Deadline {}.",
        login, title, uuid, shortDescription, longDescription, deadline);

    User user = anwenderRepository.getOne(login);
    Topic t = topicRepository.findById(uuid).get();

    if (!user.equals(t.getCreator())) {
      LOG.warn("User {} ist nicht berechtigt auf Topic {} zuzugreifen.", login, t.getTitle());
      throw new AccessDeniedException("Kein Zugriff auf das Topic!");
    }

    if (title.length() < 10 || title.length() > 60) {
      LOG.debug("Titel '{}' muss zwischen 10 und 60 Zeichen lang sein.", title);
      throw new ValidationException("Der Titel muss zwischen 10 und 60 Zeichen lang sein!");
    }

    if (shortDescription.length() < 10 || shortDescription.length() > 60) {
      LOG.debug("Kurzbeschreibung '{}' muss zwischen 10 und 60 Zeichen lang sein.",
          shortDescription);
      throw new ValidationException(
          "Die Kurzbeschreibung muss zwischen 10 und 60 Zeichen lang sein!");
    }

    if (longDescription.length() < 10 || longDescription.length() > 160) {
      LOG.debug("Langbeschreibung '{}' muss zwischen 10 und 160 Zeichen lang sein.",
          longDescription);
      throw new ValidationException(
          "Die Langbeschreibung muss zwischen 10 und 160 Zeichen lang sein!");
    }

    // Deadline valide?
    if (deadline.length() != 10) {
      LOG.debug("Deadline '{}' muss exakt 10 Zeichen lang sein.", deadline);
      throw new ValidationException("Das Format der Deadline passt nicht! (TT.MM.YYYY)");
    }

    Task task = new Task(t, title, shortDescription, longDescription, deadline);
    taskRepository.save(task);

    for (User subscriber : t.getSubscriber()) {
      statusRepository.save(new Status(task, subscriber));
      if (sendEmail) {
        MailServiceImpl.sendMail(subscriber.getEmail(), "Neuer Task: "+ task.getTitle(), "Lieber "
            + subscriber.getName()
            + ",\n\nes gibt einen neuen Task zur Bearbeitung in Topic: " + task.getTopic().getTitle() + "!"
                + "\n\nTask-Titel: " + task.getTitle() + "\nDeadline: " + task.getDeadline() + "\n\nAgenda");
      }
    }
    return task.getId();
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public SubscriberTaskDto getTask(Long taskId, String login) {
    LOG.info("User {} gibt abbonierten Task aus.", login);
    LOG.debug("User {} gibt abbonierten Task mit ID {} aus.", login, taskId);

    Task task = taskRepository.getOne(taskId);
    Topic topic = task.getTopic();
    User user = anwenderRepository.getOne(login);
    if (!(topic.getCreator().equals(user) || topic.getSubscriber().contains(user))) {
      LOG.warn("User {} ist nicht berechtigt auf Task {} zuzugreifen.", login, taskId);
      throw new AccessDeniedException("Zugriff verweigert.");
    }
    Status status = getOrCreateStatus(taskId, login);
    return mapper.createReadDto(task, status);
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public OwnerTaskDto getManagedTask(Long taskId, String login) {
    LOG.info("User {} gibt eigenen Task aus.", login);
    LOG.debug("User {} gibt eigenen Task mit ID {} aus.", login, taskId);

    Task task = taskRepository.getOne(taskId);
    Topic topic = task.getTopic();
    User createdBy = topic.getCreator();
    if (!login.equals(createdBy.getLogin())) {
      LOG.warn("User {} ist nicht berechtigt auf Task {} zuzugreifen.", login, taskId);
      throw new AccessDeniedException("Zugriff verweigert.");
    }

    OwnerTaskDto dto = mapper.createManagedDto(task);

    List<StatusDto> statuses = new ArrayList<>();

    for (Status status : statusRepository.findByTask(task)) {
      statuses.add(mapper.createDto(status));
    }

    dto.setStatus(statuses);

    return dto;
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public List<SubscriberTaskDto> getSubscribedTasks(String login, String search,
      SearchEnum searchType) {
    LOG.info("User {} gibt abbonierte Tasks aus.", login);

    User user = anwenderRepository.getOne(login);
    Collection<Topic> topics = user.getSubscriptions();

    List<SubscriberTaskDto> result = extracted(user, topics);
    // nach search filtern
    for (Iterator<SubscriberTaskDto> it = result.iterator(); it.hasNext();) {
      SubscriberTaskDto next = it.next();
      if (!next.getTitle().toLowerCase().contains(search.toLowerCase())
          && !next.getShortDescription().toLowerCase().contains(search.toLowerCase())) {
        it.remove();
      } else if (!(searchType == SearchEnum.ALLE)) {
        if (searchType == SearchEnum.OFFEN && next.getStatus().getStatus() == StatusEnum.FERTIG) {
          it.remove();
        } else if (searchType == SearchEnum.FERTIG
            && next.getStatus().getStatus() == StatusEnum.NEU) {
          it.remove();
        }
      }
    }
    return result;
  }

  // umbenennen
  private List<SubscriberTaskDto> extracted(User user, Collection<Topic> topics) {
    HashSet<String> uuids = new HashSet<>();

    if (topics.isEmpty()) {
      List<SubscriberTaskDto> result = new ArrayList<>();
      return result;
    }

    for (Topic t : topics) {
      uuids.add(t.getUuid());
      for (Task task : t.getTasks()) {
        getOrCreateStatus(task.getId(), user.getLogin());
      }
    }

    List<Status> statusFertig = statusRepository.findByUserAndStatusAndTopicOrderByTitle(user,
        Arrays.asList(StatusEnum.FERTIG), uuids);

    List<Status> statusOffen = statusRepository.findByUserAndStatusAndTopicOrderByTitle(user,
        Arrays.asList(StatusEnum.OFFEN, StatusEnum.NEU), uuids);

    List<Status> statusAll = new ArrayList<>(statusOffen);
    statusAll.addAll(statusFertig);

    List<SubscriberTaskDto> result = new ArrayList<>();
    for (Status status : statusAll) {
      result.add(mapper.createReadDto(status.getTask(), status));
    }
    return result;
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public List<SubscriberTaskDto> getTasksForTopic(String uuid, String login) {
    LOG.info("User {} gibt Tasks zu einem Topic aus.", login);
    LOG.debug("User {} gibt Tasks zu Topic {} aus.", login, uuid);

    User user = anwenderRepository.getOne(login);
    Topic topic = topicRepository.getOne(uuid);

    return extracted(user, SetUtils.hashSet(topic));
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public void checkTask(Long taskId, String login) {
    LOG.info("User {} markiert den Task mit der ID:{} als erledigt.", login, taskId);

    Status status = getOrCreateStatus(taskId, login);
    status.setStatus(StatusEnum.FERTIG);
    LOG.debug("Status von Task {} und Anwender {} gesetzt auf {}", status.getTask(),
        status.getUser(), status.getStatus());
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public void resetTask(Long taskId, String login) {
    LOG.info("User {} setzt den Task mit der ID:{} zurück.", login, taskId);

    Status status = getOrCreateStatus(taskId, login);
    status.setStatus(StatusEnum.NEU);
    LOG.debug("Status von Task {} und Anwender {} gesetzt auf {}", status.getTask(),
        status.getUser(), status.getStatus());
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public void updateTask(Long id, String login, String shortDescription, String longDescription,
      String deadline) {
    LOG.info("User {} ändert die Beschreibungen von Task mit der ID:{}.", login, id);
    LOG.debug(
        "User {} ändert von Task mit der ID:{} die Kurzbeschreibung auf '{}' und die Langbeschreibung auf '{}' und setzt die Deadline auf {}.",
        login, id, shortDescription, longDescription, deadline);

    Task t = taskRepository.getOne(id);
    User user = anwenderRepository.getOne(login);

    if (!user.equals(t.getTopic().getCreator())) {
      LOG.warn("User {} ist nicht berechtigt auf Task {} zuzugreifen.", login, id);
      throw new AccessDeniedException("Zugriff verweigert.");
    }

    if (shortDescription.length() < 10 || shortDescription.length() > 60) {
      LOG.debug("Kurzbeschreibung '{}' muss zwischen 10 und 60 Zeichen lang sein.",
          shortDescription);
      throw new ValidationException(
          "Die Kurzbeschreibung muss zwischen 10 und 60 Zeichen lang sein!");
    }

    if (longDescription.length() < 10 || longDescription.length() > 160) {
      LOG.debug("Langbeschreibung '{}' muss zwischen 10 und 160 Zeichen lang sein.",
          longDescription);
      throw new ValidationException(
          "Die Langbeschreibung muss zwischen 10 und 160 Zeichen lang sein!");
    }

    // Deadline valide?
    if (deadline.length() != 10) {
      LOG.debug("Deadline '{}' muss exakt 10 Zeichen lang sein.", deadline);
      throw new ValidationException("Das Format der Deadline passt nicht! (TT.MM.YYYY)");
    }

    t.setShortDescription(shortDescription);
    t.setLongDescription(longDescription);
    t.setDeadline(deadline);
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public List<OwnerTaskDto> getManagedTasks(String uuid, String login) {
    LOG.info("User {} gibt eigene Tasks aus.", login);
    LOG.debug("User {} gibt alle Tasks aus Topic {} aus.", login, uuid);

    List<OwnerTaskDto> result = new ArrayList<>();
    List<Task> list = topicRepository.findManagedTaskForTopic(uuid);
    for (Task task : list) {
      result.add(mapper.createManagedDto(task));
    }
    return result;
  }

  private Status getOrCreateStatus(Long taskId, String login) {
    User user = anwenderRepository.getOne(login);
    Task task = taskRepository.getOne(taskId);
    Status status = statusRepository.findByUserAndTask(user, task);
    if (status == null) {
      status = new Status(task, user);
      statusRepository.save(status);
    }
    return status;
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public void commentTask(Long id, String login, String comment) {
    LOG.info("User {} kommentiert Task mit der ID:{}.", login, id);
    LOG.debug("User {} kommentiert {} Task mit der ID:{}.", login, comment, id);

    Task task = taskRepository.getOne(id);
    User user = anwenderRepository.getOne(login);

    Status status = statusRepository.findByUserAndTask(user, task);

    status.setComment(comment);

  }

  @Override
  public StatusDto getStatus(Long taskId, String login) {
    LOG.info("User {} gibt Status zu Task mit der ID:{} aus.", login, taskId);

    Task task = taskRepository.getOne(taskId);
    User user = anwenderRepository.getOne(login);

    Status status = statusRepository.findByUserAndTask(user, task);
    StatusDto result =
        new StatusDto(status.getStatus(), status.getComment(), status.getUser().getName());

    return result;
  }
}
