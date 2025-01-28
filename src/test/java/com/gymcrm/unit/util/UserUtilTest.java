package com.gymcrm.unit.util;

import static org.junit.jupiter.api.Assertions.*;

import com.gymcrm.util.UserUtil;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

class UserUtilTest {
  @Test
  void generateUniqueUsername_ShouldReturnUniqueUsername_WhenUsernameDoesNotExist() {
    Set<String> existingUsernames = new HashSet<>();
    existingUsernames.add("john.doe1");
    existingUsernames.add("john.doe2");

    String baseUsername = "john.doe";

    String uniqueUsername = UserUtil.generateUniqueUsername(existingUsernames, baseUsername);

    assertEquals("john.doe", uniqueUsername);
  }

  @Test
  void generateUniqueUsername_ShouldReturnUniqueUsername_WhenUsernameExists() {
    Set<String> existingUsernames = new HashSet<>();
    existingUsernames.add("john.doe");
    existingUsernames.add("john.doe1");

    String baseUsername = "john.doe";

    String uniqueUsername = UserUtil.generateUniqueUsername(existingUsernames, baseUsername);

    assertEquals("john.doe2", uniqueUsername);
  }

  @Test
  void getBaseUsername_ShouldReturnFormattedUsername_WhenValidNamesAreProvided() {
    String firstName = " John ";
    String lastName = " Doe ";

    String baseUsername = UserUtil.getBaseUsername(firstName, lastName);

    assertEquals("john.doe", baseUsername);
  }

  @Test
  void getBaseUsername_ShouldThrowException_WhenFirstNameIsNull() {
    String lastName = "Doe";

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> UserUtil.getBaseUsername(null, lastName));

    assertEquals("First name cannot be null or empty.", exception.getMessage());
  }

  @Test
  void getBaseUsername_ShouldThrowException_WhenLastNameIsBlank() {
    String firstName = "John";

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> UserUtil.getBaseUsername(firstName, "   "));

    assertEquals("Last name cannot be null or empty.", exception.getMessage());
  }

  @Test
  void generatePassword_ShouldReturnPasswordOfCorrectLength() {
    String password = UserUtil.generatePassword();

    assertNotNull(password);
    assertEquals(12, password.length());
  }

  @Test
  void generatePassword_ShouldReturnAlphanumericPassword() {
    String password = UserUtil.generatePassword();

    assertTrue(StringUtils.isAlphanumeric(password), "Password should be alphanumeric");
  }
}
