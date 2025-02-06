package com.gymcrm.util;

import java.util.UUID;
import java.util.concurrent.*;

public class PasswordStorage {
	private static final ConcurrentHashMap<UUID, String> PASSWORD_MAP = new ConcurrentHashMap<>();

	public static void storePassword(UUID userId, String password) {
		PASSWORD_MAP.put(userId, password);
	}

	public static String getPassword(UUID userId) {
		return PASSWORD_MAP.get(userId);
	}

	public static void removePassword(UUID userId) {
		PASSWORD_MAP.remove(userId);
	}
}
