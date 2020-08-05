package edu.hm.cs.katz.swt2.agenda.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import javax.transaction.Transactional;
import javax.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import edu.hm.cs.katz.swt2.agenda.service.UserService;

@SpringBootTest
@ActiveProfiles("none")
@Transactional
public class UserCreationIT {

  @Autowired
  UserService userService;

  @Test
  @WithUserDetails("admin")
  public void createdUserContainsAllInformation() {
    userService.legeAn("timo", "Timo", "TimoTimo1.", false, "timo@gmail.com");

    var createdUser = userService.getUserInfo("timo");

    assertEquals("timo", createdUser.getLogin());
    assertEquals("Timo", createdUser.getName());
    assertEquals("Timo", createdUser.getDisplayName());
  }

  @Test
  @WithUserDetails("admin")
  public void canNotCreateExistingUser() {
    userService.legeAn("timo", "Timo", "TimoTimo1.", false, "timo@gmail.com");

    assertThrows(ValidationException.class, () -> {
      userService.legeAn("timo", "Timo", "TimoTimo1.", false, "timo@gmail.com");
    });
  }
}
