package edu.hm.cs.katz.swt2.agenda.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import edu.hm.cs.katz.swt2.agenda.common.StatusEnum;
import edu.hm.cs.katz.swt2.agenda.common.UuidProviderImpl;
import edu.hm.cs.katz.swt2.agenda.persistence.Status;
import edu.hm.cs.katz.swt2.agenda.persistence.StatusRepository;
import edu.hm.cs.katz.swt2.agenda.persistence.Task;
import edu.hm.cs.katz.swt2.agenda.persistence.Topic;
import edu.hm.cs.katz.swt2.agenda.persistence.TopicRepository;
import edu.hm.cs.katz.swt2.agenda.persistence.User;
import edu.hm.cs.katz.swt2.agenda.persistence.UserRepository;
import edu.hm.cs.katz.swt2.agenda.service.dto.OwnerTopicDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.SubscriberTopicDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.UserDisplayDto;

@Component
@Transactional(rollbackFor = Exception.class)
public class TopicServiceImpl implements TopicService {

  private static final Logger LOG = LoggerFactory.getLogger(TopicServiceImpl.class);

  @Autowired
  private UuidProviderImpl uuidProvider;

  @Autowired
  private UserRepository anwenderRepository;

  @Autowired
  private TopicRepository topicRepository;

  @Autowired
  private StatusRepository statusRepository;

  @Autowired
  private DtoMapper mapper;

  @Override
  @PreAuthorize("#login==authentication.name OR hasRole('ROLE_ADMIN')")
  public void deleteTopic(String uuid, String login) {
    LOG.info("User {} löscht Topic.", login);
    LOG.debug("User {} löscht Topic {}.", login, uuid);

    Topic t = topicRepository.getOne(uuid);
    User user = anwenderRepository.getOne(login);

    if (!user.equals(t.getCreator())) {
      LOG.warn("User {} ist nicht berechtigt auf das Topic {} zuzugreifen.", login, t.getTitle());
      throw new AccessDeniedException("Kein Zugriff auf das Topic!");
    }

    Collection<User> subscriber = t.getSubscriber();

    for (User sub : subscriber) {
      sub.removeSubscription(t);
    }
    topicRepository.delete(t);
  }

  @Override
  @PreAuthorize("#login==authentication.name OR hasRole('ROLE_ADMIN')")
  public void updateTopic(String uuid, String login, String title, String shortDescription,
      String longDescription) {
    LOG.info("User {} ändert die Beschreibungen von Topic {}.", login, title);
    LOG.debug(
        "User {} ändert von Topic '{}' mit der UUID:{} die Kurzbeschreibung auf '{}' und die Langbeschreibung auf '{}'.",
        login, title, uuid, shortDescription, longDescription);

    Topic t = topicRepository.getOne(uuid);
    User user = anwenderRepository.getOne(login);
    if (!user.equals(t.getCreator())) {
      LOG.warn("User {} ist nicht berechtigt auf Topic {} zuzugreifen.", login, uuid);
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

    t.setShortDescription(shortDescription);
    t.setLongDescription(longDescription);
  }

  @Override
  @PreAuthorize("#login==authentication.name OR hasRole('ROLE_ADMIN')")
  public String createTopic(String title, String shortDescription, String longDescription,
      String login) {
    LOG.info("Erstelle Topic.");
    LOG.debug("{} erstellt Topic {} mit Kurzbeschreibung '{}' und Langbeschreibung '{}'.", login,
        title, shortDescription, longDescription);

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

    String uuid = uuidProvider.getRandomUuid();
    User creator = anwenderRepository.findById(login).get();
    Topic topic = new Topic(uuid, title, shortDescription, longDescription, creator);
    topicRepository.save(topic);
    return uuid;
  }

  @Override
  @PreAuthorize("#login==authentication.name")
  public List<OwnerTopicDto> getManagedTopics(String login, String search) {
    LOG.info("Gebe alle Topics aus, die von {} erstellt wurden.", login);

    User creator = anwenderRepository.findById(login).get();
    List<Topic> managedTopics = topicRepository.findByCreatorOrderByTitleAsc(creator);
    List<OwnerTopicDto> result = new ArrayList<>();
    for (Topic topic : managedTopics) {
      result.add(mapper.createManagedDto(topic));
    }
    // nach search filtern
    for (Iterator<OwnerTopicDto> it = result.iterator(); it.hasNext();) {
      OwnerTopicDto next = it.next();
      if (!next.getTitle().toLowerCase().contains(search.toLowerCase())
          && !next.getShortDescription().toLowerCase().contains(search.toLowerCase())) {
        it.remove();
      }
    }
    return result;
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public OwnerTopicDto getManagedTopic(String topicUuid, String login) {
    LOG.info("Greife auf ein Topic von User {} zu.", login);
    LOG.debug("Greife auf Topic {} von User {} zu.", topicUuid, login);

    Topic topic = topicRepository.getOne(topicUuid);
    return mapper.createManagedDto(topic);
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public SubscriberTopicDto getTopic(String uuid, String login) {
    LOG.info("Gebe Topic aus.");
    LOG.debug("Gebe Topic mit uuid {} von User {} aus.", uuid, login);

    Topic topic = topicRepository.getOne(uuid);
    return mapper.createDto(topic);
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public void subscribe(String topicUuid, String login) {
    LOG.info("User abboniert Topic.");
    LOG.debug("User {} abboniert Topic mit uuid {}.", login, topicUuid);

    Topic topic = topicRepository.getOne(topicUuid);
    User anwender = anwenderRepository.getOne(login);
    topic.register(anwender);
  }

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public List<SubscriberTopicDto> getSubscriptions(String login, String search) {
    LOG.info("Gebe alle abbonierten Topics von User {} aus.", login);

    User subscriber = anwenderRepository.findById(login).get();
    Collection<Topic> subscriptions = topicRepository.findBySubscriberOrderByTitleAsc(subscriber);
    List<SubscriberTopicDto> result = new ArrayList<>();
    for (Topic topic : subscriptions) {
      SubscriberTopicDto dto = mapper.createDto(topic);
      dto.setCountNotDoneTasks(statusRepository.countByStatusFindByTopicAndUserWhereStatusIn(
          Arrays.asList(StatusEnum.NEU, StatusEnum.OFFEN), dto.getUuid(), subscriber));
      result.add(dto);
    }
    // nach search filtern
    for (Iterator<SubscriberTopicDto> it = result.iterator(); it.hasNext();) {
      SubscriberTopicDto next = it.next();
      if (!next.getTitle().toLowerCase().contains(search.toLowerCase())
          && !next.getShortDescription().toLowerCase().contains(search.toLowerCase())) {
        it.remove();
      }
    }
    return result;
  }

  @Override
  public String getTopicUuid(String key) {
    LOG.info("Uuid auflösen für Key {}.", key);
    
    if (key.length() != 8) {
      LOG.debug("Key '{}' muss 8 Zeichen lang sein.", key);
      throw new ValidationException("Der Key muss 8 Zeichen lang sein!");
    }
    Topic topic = topicRepository.findByUuidEndingWith(key);

    if (topic == null) {
      LOG.debug("Kein Topic mit dem Schlüssel {} gefunden.", key);
      throw new ValidationException("Ein Topic mit diesem Schlüssel existiert nicht!");
    }
    return topic.getUuid();
  }


  @Override
  public List<UserDisplayDto> getSubscribersWithCompletedTasks(String uuid, String name) {
    LOG.info("Gebe alle Abonnenten zu Topic-UUID {} aus.", uuid);

    List<UserDisplayDto> result = new ArrayList<>();
    for (User user : topicRepository.getOne(uuid).getSubscriber()) {
      result
          .add(mapper.createDto(user, statusRepository.countByStatusFindByTopicAndUserWhereStatusIn(
              Arrays.asList(StatusEnum.FERTIG), uuid, user)));
    }
    return result;
  }

  @Override
  public void unsubscribe(String topicUuid, String login) {
    LOG.info("User {} deabonniert Topic.", login);
    LOG.debug("User {} deabonniert Topic mit UUID: {}.", login, topicUuid);
    
    User user = anwenderRepository.getOne(login);
    Topic topic = topicRepository.getOne(topicUuid);
    user.removeSubscription(topic);
    topic.unsubscribe(user);
    for (Task task : topic.getTasks()) {
      Status status = statusRepository.findByUserAndTask(user, task);
      statusRepository.delete(status);
    }
  }

  @Override
  public boolean isSubscriberOfTopic(String uuid, String login) {
    LOG.info("Überprüfe ob User {} ein Subscriber des Topics mit ID: {} ist.", login, uuid);
    
    User user = anwenderRepository.getOne(login);
    Topic topic = topicRepository.getOne(uuid);
    if (topic.getSubscriber().contains(user)) {
      return true;
    }
    return false;
  }

  @Override
  public boolean isCreatorOfTopic(String uuid, String login) {
    LOG.info("Überprüfe ob User {} Ersteller des Topics mit ID: {} ist.", login, uuid);
    
    User user = anwenderRepository.getOne(login);
    Topic topic = topicRepository.getOne(uuid);
    if (topic.getCreator().equals(user)) {
      return true;
    }
    return false;
  }
}