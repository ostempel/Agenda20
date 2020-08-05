package edu.hm.cs.katz.swt2.agenda.common;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UuidProviderImpl {

  public String getRandomUuid() {
    return UUID.randomUUID().toString();
  }
}
