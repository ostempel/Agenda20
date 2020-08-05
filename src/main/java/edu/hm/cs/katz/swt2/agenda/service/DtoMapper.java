package edu.hm.cs.katz.swt2.agenda.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import edu.hm.cs.katz.swt2.agenda.common.StatusEnum;
import edu.hm.cs.katz.swt2.agenda.persistence.Status;
import edu.hm.cs.katz.swt2.agenda.persistence.StatusRepository;
import edu.hm.cs.katz.swt2.agenda.persistence.Task;
import edu.hm.cs.katz.swt2.agenda.persistence.Topic;
import edu.hm.cs.katz.swt2.agenda.persistence.TopicRepository;
import edu.hm.cs.katz.swt2.agenda.persistence.User;
import edu.hm.cs.katz.swt2.agenda.persistence.UserRegistration;
import edu.hm.cs.katz.swt2.agenda.service.dto.OwnerTaskDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.OwnerTopicDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.StatusDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.SubscriberTaskDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.SubscriberTopicDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.UserDisplayDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.UserManagementDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.UserRegistrationDto;

/**
 * Hilfskomponente zum Erstellen der Transferobjekte aus den Entities. Für diese Aufgabe gibt es
 * viele Frameworks, die aber zum Teil recht schwer zu verstehen sind. Da das Mapping sonst zu viel
 * redundantem Code führt, ist die Zusammenführung aber notwendig.
 * 
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
@Component
public class DtoMapper {

  @Autowired
  private ModelMapper mapper;

  @Autowired
  private TopicRepository topicRepository;

  @Autowired
  private StatusRepository statusRepository;
  
  /**
   * Erstellt ein {@link UserRegistrationDto} aus einer {@link UserRegistration}.
   */
  public UserRegistrationDto createDto(UserRegistration user) {
    UserRegistrationDto dto = mapper.map(user, UserRegistrationDto.class);
    return dto;
  }

  /**
   * Erstellt ein {@link UserDisplayDto} aus einem {@link User}.
   */
  public UserDisplayDto createDto(User user) {
    UserDisplayDto dto = mapper.map(user, UserDisplayDto.class);
    dto.setTopicCount(topicRepository.countByCreator(user));
    dto.setSubscriptionCount(user.getSubscriptions().size());
    return dto;
  }

  /**
   * Erstellt ein {@link UserManagementDto} aus einem {@link User}.
   */
  public UserManagementDto createManagementDto(User user) {
    UserManagementDto dto = mapper.map(user, UserManagementDto.class);
    return dto;
  }

  /**
   * Erstellt ein {@link UserDisplayDto} aus einem {@link User und Topic} und setzt die Anzahl
   * abgeschlossener Tasks zu diesem Topic.
   */
  public UserDisplayDto createDto(User user, int completedTasks) {
    UserDisplayDto dto = mapper.map(user, UserDisplayDto.class);
    dto.setCompletedTasks(completedTasks);
    return dto;
  }

  /**
   * Erstellt ein {@link SubscriberTopicDto} aus einem {@link Topic}.
   */
  public SubscriberTopicDto createDto(Topic topic) {
    UserDisplayDto creatorDto = mapper.map(topic.getCreator(), UserDisplayDto.class);
    SubscriberTopicDto topicDto = new SubscriberTopicDto(topic.getUuid(), creatorDto,
        topic.getTitle(), topic.getShortDescription(), topic.getLongDescription());
    return topicDto;
  }

  /**
   * Erstellt ein {@link StatusDto} aus einem {@link Status}.
   */
  public StatusDto createDto(Status status) {
    return new StatusDto(status.getStatus(), status.getComment(), status.getUser().getName());
  }

  /**
   * Erstellt ein {@link SubscriberTaskDto} aus einem {@link Task} und einem {@link Status}.
   */
  public SubscriberTaskDto createReadDto(Task task, Status status) {
    Topic topic = task.getTopic();
    SubscriberTopicDto topicDto = createDto(topic);
    return new SubscriberTaskDto(task.getId(), task.getTitle(), task.getShortDescription(),
        task.getLongDescription(), topicDto, createDto(status), task.getDeadline());
  }

  /**
   * Erstellt ein {@link OwnerTopicDto} aus einem {@link Topic}.
   */
  public OwnerTopicDto createManagedDto(Topic topic) {
    boolean isSubscribed = false;

    if (!topic.getSubscriber().isEmpty()) {
      isSubscribed = true;
    }

    return new OwnerTopicDto(topic.getUuid(), createDto(topic.getCreator()), topic.getTitle(),
        topic.getShortDescription(), topic.getLongDescription(), isSubscribed,
        topic.getSubscriber(), topic.getTasks().size());
  }

  /**
   * Erstellt ein {@link OwnerTaskDto} aus einem {@link Task}.
   */
  public OwnerTaskDto createManagedDto(Task task) {
    long countDoneUser = statusRepository
        .countByStatusFindByTaskWhereStatusIsFertig(StatusEnum.FERTIG, task.getId());
    return new OwnerTaskDto(task.getId(), task.getTitle(), task.getShortDescription(),
        task.getLongDescription(), createDto(task.getTopic()), countDoneUser, task.getDeadline());
  }
}