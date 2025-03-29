package com.gymcrm.unit.trainer.adapter.output.messaging;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.trainer.adapter.output.messaging.PendingResponsesCleaner;
import com.gymcrm.trainer.adapter.output.messaging.TrainerWorkloadMessagingAdapter;
import com.gymcrm.trainer.adapter.output.queue.message.TrainerWorkloadResponseMessage;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PendingResponsesCleanerTest {
	@Mock
	private TrainerWorkloadMessagingAdapter messagingAdapter;

	@InjectMocks
	private PendingResponsesCleaner pendingResponsesCleaner;

	private ConcurrentHashMap<String, CompletableFuture<TrainerWorkloadResponseMessage>> pendingResponses;

	@BeforeEach
	void setUp() {
		pendingResponses = new ConcurrentHashMap<>();
		when(messagingAdapter.getPendingResponses()).thenReturn(pendingResponses);
	}

	@Test
	void cleanupExpiredPendingResponses_ShouldDoNothing_WhenNoResponses() {
		pendingResponsesCleaner.cleanupExpiredPendingResponses();

		verify(messagingAdapter).getPendingResponses();
	}

	@Test
	void cleanupExpiredPendingResponses_ShouldRemoveCompletedFutures() {
		CompletableFuture<TrainerWorkloadResponseMessage> completedFuture = CompletableFuture
		        .completedFuture(new TrainerWorkloadResponseMessage());
		pendingResponses.put("completed-transaction", completedFuture);

		pendingResponsesCleaner.cleanupExpiredPendingResponses();

		verify(messagingAdapter).getPendingResponses();
		assertTrue(pendingResponses.isEmpty());
	}

	@Test
	void cleanupExpiredPendingResponses_ShouldRemoveCancelledFutures() {
		CompletableFuture<TrainerWorkloadResponseMessage> cancelledFuture = new CompletableFuture<>();
		cancelledFuture.cancel(true);
		pendingResponses.put("cancelled-transaction", cancelledFuture);

		pendingResponsesCleaner.cleanupExpiredPendingResponses();

		verify(messagingAdapter).getPendingResponses();
		assertTrue(pendingResponses.isEmpty());
	}

	@Test
	void cleanupExpiredPendingResponses_ShouldRemoveExceptionallyCompletedFutures() {
		CompletableFuture<TrainerWorkloadResponseMessage> exceptionalFuture = new CompletableFuture<>();
		exceptionalFuture.completeExceptionally(new RuntimeException("Test exception"));
		pendingResponses.put("exceptional-transaction", exceptionalFuture);

		pendingResponsesCleaner.cleanupExpiredPendingResponses();

		verify(messagingAdapter).getPendingResponses();
		assertTrue(pendingResponses.isEmpty());
	}

	@Test
	void cleanupExpiredPendingResponses_ShouldKeepPendingFutures() {
		CompletableFuture<TrainerWorkloadResponseMessage> pendingFuture = new CompletableFuture<>();
		pendingResponses.put("pending-transaction", pendingFuture);

		pendingResponsesCleaner.cleanupExpiredPendingResponses();

		verify(messagingAdapter).getPendingResponses();
		assertEquals(1, pendingResponses.size());
		assertTrue(pendingResponses.containsKey("pending-transaction"));
	}

	@Test
	void cleanupExpiredPendingResponses_ShouldHandleMultipleFuturesCorrectly() {
		CompletableFuture<TrainerWorkloadResponseMessage> completedFuture = CompletableFuture
		        .completedFuture(new TrainerWorkloadResponseMessage());
		CompletableFuture<TrainerWorkloadResponseMessage> cancelledFuture = new CompletableFuture<>();
		cancelledFuture.cancel(true);
		CompletableFuture<TrainerWorkloadResponseMessage> exceptionalFuture = new CompletableFuture<>();
		exceptionalFuture.completeExceptionally(new RuntimeException("Test exception"));
		CompletableFuture<TrainerWorkloadResponseMessage> pendingFuture = new CompletableFuture<>();

		pendingResponses.put("completed-transaction", completedFuture);
		pendingResponses.put("cancelled-transaction", cancelledFuture);
		pendingResponses.put("exceptional-transaction", exceptionalFuture);
		pendingResponses.put("pending-transaction", pendingFuture);

		pendingResponsesCleaner.cleanupExpiredPendingResponses();

		verify(messagingAdapter).getPendingResponses();
		assertEquals(1, pendingResponses.size());
		assertTrue(pendingResponses.containsKey("pending-transaction"));
		assertFalse(pendingResponses.containsKey("completed-transaction"));
		assertFalse(pendingResponses.containsKey("cancelled-transaction"));
		assertFalse(pendingResponses.containsKey("exceptional-transaction"));
	}

	@Test
	void cleanupExpiredPendingResponses_ShouldHandleEmptyMapGracefully() {
		PendingResponsesCleaner spy = spy(pendingResponsesCleaner);

		spy.cleanupExpiredPendingResponses();

		verify(messagingAdapter).getPendingResponses();
		verify(spy).cleanupExpiredPendingResponses();
	}
}
