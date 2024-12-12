package com.gymcrm.util;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UUIDGenerator implements UUIDGeneratorInterface {
  @Override
  public UUID newUUID() {
    return UUID.randomUUID();
  }
}
