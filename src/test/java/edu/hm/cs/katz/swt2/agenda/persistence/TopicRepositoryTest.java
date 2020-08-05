package edu.hm.cs.katz.swt2.agenda.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class TopicRepositoryTest {
  
  @Autowired
  UserRepository userRepository;
  
  @Autowired
  TopicRepository topicRepository;
  
  private static final String UUID_A = "111111111111111111111111111111111111";
  private static final String UUID_B = "111111111111111111111111111111111112";
  private static final String UUID_C = "111111111111111111111111111111111113";
  
  @Test
  public void topicRepositoryDeliversTopicsOrdered() {
    User user = new User("time", "Timo", "TimoTimo1.", false, "timo@gmail.com");
    userRepository.save(user);
    
    Topic a = new Topic(UUID_A, "Erstes Topic zum testen", "Erstes Topic zum testen", "Erstes Topic zum testen", user);
    topicRepository.save(a);
    
    Topic b = new Topic(UUID_B, "Zweites Topic zum testen", "Erstes Topic zum testen", "Erstes Topic zum testen", user);
    topicRepository.save(b);
    
    Topic c = new Topic(UUID_C, "Drittes Topic zum testen", "Erstes Topic zum testen", "Erstes Topic zum testen", user);
    topicRepository.save(c);
    
    List<Topic> topics = topicRepository.findByCreatorOrderByTitleAsc(user);
    
    assertEquals(3, topics.size());
    
    assertEquals(c, topics.get(0));
    assertEquals(a, topics.get(1));
    assertEquals(b, topics.get(2));
  }
}
