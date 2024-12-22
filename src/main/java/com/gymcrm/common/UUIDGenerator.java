package com.gymcrm.common;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UUIDGenerator implements UUIDGeneratorInterface {
  @Override
  public UUID newUUID() {
    return UUID.randomUUID();
  }
}
