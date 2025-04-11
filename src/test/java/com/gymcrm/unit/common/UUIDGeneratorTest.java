package com.gymcrm.unit.common;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import com.gymcrm.common.UUIDGenerator;
import com.gymcrm.common.UUIDGeneratorInterface;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UUIDGeneratorTest {
	@Mock
	private UUIDGeneratorInterface mockUUIDGenerator;

	@InjectMocks
	private UUIDGenerator uuidGenerator;

	@Test
	void newUUID_ShouldReturnNonNullUUID() {
		UUID result = uuidGenerator.newUUID();

		assertNotNull(result, "Generated UUID should not be null");
	}

	@Test
	void newUUID_ShouldBeCalledOnce_WhenMocked() {
		UUID expectedUUID = UUID.randomUUID();
		when(mockUUIDGenerator.newUUID()).thenReturn(expectedUUID);

		UUID result = mockUUIDGenerator.newUUID();

		verify(mockUUIDGenerator, times(1)).newUUID();
		assertNotNull(result, "Mocked UUID should not be null");
	}
}
