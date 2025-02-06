package com.gymcrm.unit.configuration.security.protection;

import static org.junit.jupiter.api.Assertions.*;

import com.gymcrm.configuration.security.protection.BruteForceProtectionService;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BruteForceProtectionServiceTest {
    private BruteForceProtectionService bruteForceProtectionService;
    private final String username = "testUser";

    @BeforeEach
    void setUp() {
        bruteForceProtectionService = new BruteForceProtectionService();
    }

    @Test
    void shouldNotBlockUserInitially() {
        assertFalse(bruteForceProtectionService.isBlocked(username));
    }

    @Test
    void shouldBlockUserAfterThreeFailedAttempts() {
        bruteForceProtectionService.loginFailed(username);
        bruteForceProtectionService.loginFailed(username);
        bruteForceProtectionService.loginFailed(username);

        assertTrue(bruteForceProtectionService.isBlocked(username));
    }

    @Test
    void shouldNotBlockUserBeforeMaxAttempts() {
        bruteForceProtectionService.loginFailed(username);
        bruteForceProtectionService.loginFailed(username);

        assertFalse(bruteForceProtectionService.isBlocked(username));
    }

    @Test
    void shouldUnblockUserAfterLockTimeExpires() throws Exception {
        bruteForceProtectionService.loginFailed(username);
        bruteForceProtectionService.loginFailed(username);
        bruteForceProtectionService.loginFailed(username);

        setLockTime(LocalDateTime.now().minusSeconds(1));

        assertFalse(bruteForceProtectionService.isBlocked(username));
    }

    @Test
    void shouldNotAllowLoginWhenUserIsStillBlocked() throws Exception {
        bruteForceProtectionService.loginFailed(username);
        bruteForceProtectionService.loginFailed(username);
        bruteForceProtectionService.loginFailed(username);

        setLockTime(LocalDateTime.now().plusSeconds(300));

        assertTrue(bruteForceProtectionService.isBlocked(username));
    }

    private void setLockTime(LocalDateTime lockTime) throws Exception {
        Field attemptsCacheField = BruteForceProtectionService.class.getDeclaredField("attemptsCache");
        attemptsCacheField.setAccessible(true);

        @SuppressWarnings("unchecked")
        Map<String, Object> attemptsCache = (Map<String, Object>) attemptsCacheField.get(bruteForceProtectionService);

        Object failedLoginAttempt = attemptsCache.get(username);
        if (failedLoginAttempt != null) {
            Field lockTimeField = failedLoginAttempt.getClass().getDeclaredField("lockTime");
            lockTimeField.setAccessible(true);
            lockTimeField.set(failedLoginAttempt, lockTime);
        }
    }
}
