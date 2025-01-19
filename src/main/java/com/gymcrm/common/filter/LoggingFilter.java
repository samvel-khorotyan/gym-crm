package com.gymcrm.common.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class LoggingFilter implements Filter {
	private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException {
		if (!(request instanceof HttpServletRequest httpRequest)) {
			throw new ServletException("Request is not an instance of HttpServletRequest");
		}

		String transactionId = MDC.get("transactionId");
		if (transactionId == null) {
			transactionId = "N/A";
		}

		try {
			logger.info("REST Call - Method: {}, URI: {}, transactionId: {}", httpRequest.getMethod(),
			        httpRequest.getRequestURI(), transactionId);
			chain.doFilter(request, response);
		} catch (Exception e) {
			logger.error("Error during filter chain execution", e);
			throw new ServletException("Error during filter execution", e);
		}
	}
}
