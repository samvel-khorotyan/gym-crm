package com.gymcrm.unit.util;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.gymcrm.util.UUIDGenerator;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class UUIDGeneratorTest {
  @Test
  void newUUID_shouldGenerateUniqueUUID() {
    UUIDGenerator uuidGenerator = new UUIDGenerator();

    UUID uuid1 = uuidGenerator.newUUID();
    UUID uuid2 = uuidGenerator.newUUID();

    assertNotNull(uuid1);
    assertNotNull(uuid2);
    assertNotEquals(uuid1, uuid2);
  }
}
