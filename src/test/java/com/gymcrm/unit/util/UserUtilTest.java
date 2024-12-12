package com.gymcrm.unit.util;

import static org.junit.jupiter.api.Assertions.*;

import com.gymcrm.util.UserUtil;
import org.junit.jupiter.api.Test;

class UserUtilTest {
  @Test
  void generateUsername_shouldGenerateUniqueUsernameForFirstAndLastName() {
    String username1 = UserUtil.generateUsername("John", "Doe");
    String username2 = UserUtil.generateUsername("John", "Doe");

    assertEquals("john.doe", username1);
    assertEquals("john.doe1", username2);
  }

  @Test
  void generateUsername_shouldThrowExceptionForNullFirstName() {
    assertThrows(IllegalArgumentException.class, () -> UserUtil.generateUsername(null, "Doe"));
  }

  @Test
  void generateUsername_shouldThrowExceptionForBlankFirstName() {
    assertThrows(IllegalArgumentException.class, () -> UserUtil.generateUsername("   ", "Doe"));
  }

  @Test
  void generateUsername_shouldThrowExceptionForNullLastName() {
    assertThrows(IllegalArgumentException.class, () -> UserUtil.generateUsername("John", null));
  }

  @Test
  void generateUsername_shouldThrowExceptionForBlankLastName() {
    assertThrows(IllegalArgumentException.class, () -> UserUtil.generateUsername("John", "   "));
  }

  @Test
  void generatePassword_shouldGenerate12CharacterPassword() {
    String password = UserUtil.generatePassword();

    assertNotNull(password);
    assertEquals(12, password.length());
    assertTrue(password.matches("[a-zA-Z0-9]+")); // Check if alphanumeric
  }
}
