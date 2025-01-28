package com.gymcrm.util;

import java.util.Set;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

public class UserUtil {
  public static String generateUniqueUsername(Set<String> usernames, String baseUsername) {
    int count = 0;
    String candidateUsername = baseUsername;

    while (usernames.contains(candidateUsername)) {
      count++;
      candidateUsername = baseUsername + count;
    }

    return candidateUsername;
  }

  public static String getBaseUsername(String firstName, String lastName) {
    validateName(firstName, "First name");
    validateName(lastName, "Last name");

    return (firstName.trim() + "." + lastName.trim()).toLowerCase();
  }

  public static String generatePassword() {
    return RandomStringUtils.randomAlphanumeric(12);
  }

  private static void validateName(String name, String fieldName) {
    if (StringUtils.isBlank(name)) {
      throw new IllegalArgumentException(fieldName + " cannot be null or empty.");
    }
  }
}
