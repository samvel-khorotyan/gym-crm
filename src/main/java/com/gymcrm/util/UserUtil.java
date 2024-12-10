package com.gymcrm.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

public class UserUtil {
  private static final Map<String, Integer> USERNAME_TRACKER = new ConcurrentHashMap<>();

  public static String generateUsername(String firstName, String lastName) {
    validateName(firstName, "First name");
    validateName(lastName, "Last name");

    String baseUsername = (firstName.trim() + "." + lastName.trim()).toLowerCase();
    int count = USERNAME_TRACKER.getOrDefault(baseUsername, 0);
    USERNAME_TRACKER.put(baseUsername, count + 1);

    return count == 0 ? baseUsername : baseUsername + count;
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
