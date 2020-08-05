package edu.hm.cs.katz.swt2.agenda.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import javax.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import edu.hm.cs.katz.swt2.agenda.persistence.User;
import edu.hm.cs.katz.swt2.agenda.persistence.UserRegistrationRepository;
import edu.hm.cs.katz.swt2.agenda.persistence.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @Mock
  UserRepository userRepository;

  @Mock
  UserRegistrationRepository userRegistrationRepository;
  
  @InjectMocks
  UserService userService = new UserServiceImpl();

  @Test
  public void createUserSuccess() {
    Mockito.when(userRepository.existsById("timo")).thenReturn(false);
    
    userService.legeAn("timo", "Timo", "TimoTimo1.", false, "timo@gmail.com");

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    Mockito.verify(userRepository).save(userCaptor.capture());
    assertEquals("timo", userCaptor.getValue().getLogin());
  }

  @Test
  public void shouldFailIfUserExists() {
    Mockito.when(userRepository.existsById("timo")).thenReturn(true);

    assertThrows(ValidationException.class, () -> {
      userService.legeAn("timo", "Timo", "TimoTimo1.", false, "timo@gmail.com");
    });
  }
  
  @Test
  public void shouldFailIfLoginTooShort() {
    assertThrows(ValidationException.class, () -> {
      userService.legeAn("tim", "Timo", "TimoTimo1.", false, "timo@gmail.com");
    });
  }
  
  @Test
  public void shouldFailIfPasswordContainsNoSpecialChar() {
    assertThrows(ValidationException.class, () -> {
      userService.legeAn("timo", "Timo", "TimoTimo1", false, "timo@gmail.com");
    });
  }
  
  @Test
  public void shouldFailIfEmailContainsNoAt() {
    assertThrows(ValidationException.class, () -> {
      userService.legeAn("timo", "Timo", "TimoTimo1", false, "timogmail.com");
    });
  }
}
