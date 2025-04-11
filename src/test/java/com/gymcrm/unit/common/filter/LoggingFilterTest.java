package com.gymcrm.unit.common.filter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.common.filter.LoggingFilter;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

class LoggingFilterTest {
	private LoggingFilter loggingFilter;
	private HttpServletRequest mockRequest;
	private ServletResponse mockResponse;
	private FilterChain mockChain;

	@BeforeEach
	void setUp() {
		loggingFilter = new LoggingFilter();
		mockRequest = mock(HttpServletRequest.class);
		mockResponse = mock(ServletResponse.class);
		mockChain = mock(FilterChain.class);
	}

	@Test
  void doFilter_ValidHttpRequest_ShouldLogAndProceed() throws ServletException, IOException {
    when(mockRequest.getMethod()).thenReturn("GET");
    when(mockRequest.getRequestURI()).thenReturn("/api/test");
    MDC.put("transactionId", "12345");

    loggingFilter.doFilter(mockRequest, mockResponse, mockChain);

    verify(mockChain, times(1)).doFilter(mockRequest, mockResponse);
    MDC.clear();
  }

	@Test
  void doFilter_MissingTransactionId_ShouldLogWithDefaultTransactionId()
      throws ServletException, IOException {
    when(mockRequest.getMethod()).thenReturn("POST");
    when(mockRequest.getRequestURI()).thenReturn("/api/create");

    loggingFilter.doFilter(mockRequest, mockResponse, mockChain);

    verify(mockChain, times(1)).doFilter(mockRequest, mockResponse);
    MDC.clear();
  }

	@Test
	void doFilter_NonHttpRequest_ShouldThrowServletException() {
		ServletRequest nonHttpRequest = mock(ServletRequest.class);

		ServletException exception = assertThrows(ServletException.class,
		        () -> loggingFilter.doFilter(nonHttpRequest, mockResponse, mockChain));

		assertEquals("Request is not an instance of HttpServletRequest", exception.getMessage());
	}

	@Test
  void doFilter_FilterChainThrowsException_ShouldLogError() throws IOException, ServletException {
    when(mockRequest.getMethod()).thenReturn("DELETE");
    when(mockRequest.getRequestURI()).thenReturn("/api/delete");
    doThrow(new IOException("Chain error")).when(mockChain).doFilter(mockRequest, mockResponse);

    ServletException exception =
        assertThrows(
            ServletException.class,
            () -> loggingFilter.doFilter(mockRequest, mockResponse, mockChain));

    assertEquals("Error during filter execution", exception.getMessage());
  }
}
