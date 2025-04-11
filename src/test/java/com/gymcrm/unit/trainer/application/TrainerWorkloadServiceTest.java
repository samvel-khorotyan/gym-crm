package com.gymcrm.unit.trainer.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.trainer.adapter.input.web.response.TrainerMonthlyWorkloadResponse;
import com.gymcrm.trainer.application.TrainerWorkloadService;
import com.gymcrm.trainer.application.port.output.LoadTrainerWorkloadPort;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadServiceTest {
	@Mock
	private LoadTrainerWorkloadPort loadTrainerWorkloadPort;

	@InjectMocks
	private TrainerWorkloadService trainerWorkloadService;

	private String username;
	private int year;
	private int month;
	private TrainerMonthlyWorkloadResponse expectedResponse;

	@BeforeEach
	void setUp() {
		username = "john.doe";
		year = 2023;
		month = 5;

		expectedResponse = TrainerMonthlyWorkloadResponse.builder().username(username).firstName("John").lastName("Doe")
		        .isActive(true).year(year).month(month).summaryDuration(120).build();
	}

	@Test
  void loadTrainerMonthlyWorkload_ShouldReturnWorkload_WhenDataExists() {
    when(loadTrainerWorkloadPort.getTrainerMonthlyWorkload(username, year, month))
        .thenReturn(expectedResponse);

    TrainerMonthlyWorkloadResponse result =
        trainerWorkloadService.loadTrainerMonthlyWorkload(username, year, month);

    assertNotNull(result, "Result should not be null");
    assertEquals(username, result.getUsername(), "Username should match");
    assertEquals(year, result.getYear(), "Year should match");
    assertEquals(month, result.getMonth(), "Month should match");
    assertEquals(120, result.getSummaryDuration(), "Summary duration should match");
    assertEquals("John", result.getFirstName(), "First name should match");
    assertEquals("Doe", result.getLastName(), "Last name should match");
    assertEquals(true, result.getIsActive(), "Active status should match");

    verify(loadTrainerWorkloadPort, times(1)).getTrainerMonthlyWorkload(username, year, month);
  }

	@Test
	void loadTrainerCurrentMonthWorkload_ShouldUseCurrentDate() {
		LocalDate now = LocalDate.now();
		int currentYear = now.getYear();
		int currentMonth = now.getMonthValue();

		TrainerMonthlyWorkloadResponse currentMonthResponse = TrainerMonthlyWorkloadResponse.builder()
		        .username(username).year(currentYear).month(currentMonth).summaryDuration(150).build();

		when(loadTrainerWorkloadPort.getTrainerMonthlyWorkload(username, currentYear, currentMonth))
		        .thenReturn(currentMonthResponse);

		TrainerMonthlyWorkloadResponse result = trainerWorkloadService.loadTrainerCurrentMonthWorkload(username);

		assertNotNull(result, "Result should not be null");
		assertEquals(username, result.getUsername(), "Username should match");
		assertEquals(currentYear, result.getYear(), "Year should be current year");
		assertEquals(currentMonth, result.getMonth(), "Month should be current month");
		assertEquals(150, result.getSummaryDuration(), "Summary duration should match");

		verify(loadTrainerWorkloadPort, times(1)).getTrainerMonthlyWorkload(username, currentYear, currentMonth);
	}
}
