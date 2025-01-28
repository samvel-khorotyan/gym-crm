package com.gymcrm.unit.configuration.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.common.exception.UnauthorizedException;
import com.gymcrm.common.request.RequestContext;
import com.gymcrm.configuration.security.AuthenticationAspect;
import com.gymcrm.user.application.port.input.AuthenticationUseCase;
import com.gymcrm.user.application.port.input.LoadUserUseCase;
import com.gymcrm.user.domain.User;
import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
class AuthenticationAspectTest {
	@Mock
	private AuthenticationUseCase authenticationUseCase;

	@Mock
	private LoadUserUseCase loadUserUseCase;

	@Mock
	private RequestContext requestContext;

	@Mock
	private HttpServletRequest mockRequest;

	@Mock
	private ServletRequestAttributes mockAttributes;

	@Mock
	private JoinPoint mockJoinPoint;

	@Mock
	private MethodSignature mockMethodSignature;

	@InjectMocks
	private AuthenticationAspect authenticationAspect;

	@BeforeEach
	void setUp() {
		RequestContextHolder.setRequestAttributes(mockAttributes);
		lenient().when(mockAttributes.getRequest()).thenReturn(mockRequest);
	}

	@Test
  void authenticateRequest_ValidCredentials_ShouldAuthenticateAndSetContext() {
    when(mockRequest.getHeader("auth_username")).thenReturn("validUser");
    when(mockRequest.getHeader("auth_password")).thenReturn("validPassword");

    User mockUser = new User();
    when(loadUserUseCase.loadUserByUsername("validUser")).thenReturn(mockUser);

    authenticationAspect.authenticateRequest();

    verify(authenticationUseCase).authenticate("validUser", "validPassword");
    verify(requestContext).setUsername("validUser");
  }

	@Test
  void authenticateRequest_MissingHeaders_ShouldThrowUnauthorizedException() {
    when(mockRequest.getHeader("auth_username")).thenReturn(null);

    UnauthorizedException exception =
        assertThrows(UnauthorizedException.class, () -> authenticationAspect.authenticateRequest());

    assertEquals("Missing username or password in headers.", exception.getMessage());
  }
}
