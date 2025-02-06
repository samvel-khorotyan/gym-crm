package com.gymcrm.unit.util;

import com.gymcrm.util.PasswordStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class PasswordStorageTest {
    private UUID userId;
    private String password;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        password = "SecurePassword123!";
    }

    @Test
    void shouldStoreAndRetrievePasswordSuccessfully() {
        PasswordStorage.storePassword(userId, password);

        String retrievedPassword = PasswordStorage.getPassword(userId);
        assertNotNull(retrievedPassword);
        assertEquals(password, retrievedPassword);
    }

    @Test
    void shouldReturnNullWhenPasswordNotStored() {
        assertNull(PasswordStorage.getPassword(UUID.randomUUID()));
    }

    @Test
    void shouldRemovePasswordSuccessfully() {
        PasswordStorage.storePassword(userId, password);

        PasswordStorage.removePassword(userId);

        assertNull(PasswordStorage.getPassword(userId));
    }

    @Test
    void shouldHandleConcurrentAccessCorrectly() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        UUID testUserId = UUID.randomUUID();
        String testPassword = "ThreadSafePassword";

        Future<?> storeFuture = executor.submit(() -> PasswordStorage.storePassword(testUserId, testPassword));

        Future<String> getFuture = executor.submit(() -> PasswordStorage.getPassword(testUserId));

        storeFuture.get();
        String retrievedPassword = getFuture.get();

        assertNotNull(retrievedPassword);
        assertEquals(testPassword, retrievedPassword);

        executor.shutdown();
    }

    @Test
    void shouldNotThrowExceptionWhenRemovingNonExistentPassword() {
        assertDoesNotThrow(() -> PasswordStorage.removePassword(UUID.randomUUID()));
    }
}