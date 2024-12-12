package com.gymcrm.unit.dao;

import static org.junit.jupiter.api.Assertions.*;

import com.gymcrm.common.ApplicationProperties;
import com.gymcrm.dao.Storage;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StorageTest {
  @Mock private ApplicationProperties applicationProperties;

  @InjectMocks private Storage storage;

  @Test
  void testGetNamespace_withValidNamespaces() {
    assertNotNull(storage.getNamespace("trainee"), "Trainee namespace should not be null");
    assertNotNull(storage.getNamespace("trainer"), "Trainer namespace should not be null");
    assertNotNull(storage.getNamespace("training"), "Training namespace should not be null");
  }

  @Test
  void testGetNamespace_withInvalidNamespace() {
    assertNull(storage.getNamespace("invalid"), "Invalid namespace should return null");
  }

  @ParameterizedTest
  @ValueSource(strings = {"trainee", "trainer", "training"})
  void testGetNamespace_initialStateIsEmpty(String namespace) {
    Map<UUID, Object> namespaceMap = storage.getNamespace(namespace);
    assertTrue(namespaceMap.isEmpty(), namespace + " namespace should be empty initially");
  }

  @Test
  void testGetNamespace_afterAddingData() {
    UUID mockId1 = UUID.randomUUID();
    UUID mockId2 = UUID.randomUUID();
    Object mockEntity1 = new Object();
    Object mockEntity2 = new Object();

    Map<UUID, Object> traineeNamespace = storage.getNamespace("trainee");
    traineeNamespace.put(mockId1, mockEntity1);
    traineeNamespace.put(mockId2, mockEntity2);

    assertEquals(2, traineeNamespace.size(), "Trainee namespace should contain two items");
    assertSame(
        mockEntity1, traineeNamespace.get(mockId1), "Trainee namespace should contain mockEntity1");
    assertSame(
        mockEntity2, traineeNamespace.get(mockId2), "Trainee namespace should contain mockEntity2");
  }
}
