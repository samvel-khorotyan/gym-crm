package com.gymcrm.configuration.security;

import com.gymcrm.common.exception.ForbiddenException;
import com.gymcrm.common.exception.UnauthorizedException;
import com.gymcrm.common.request.RequestContext;
import com.gymcrm.configuration.PermissionConfig;
import com.gymcrm.user.application.port.input.AuthenticationUseCase;
import com.gymcrm.user.application.port.input.LoadUserUseCase;
import com.gymcrm.user.domain.User;
import com.gymcrm.user.domain.UserType;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class AuthenticationAspect {
  private final AuthenticationUseCase authenticationUseCase;
  private final LoadUserUseCase loadUserUseCase;
  private final RequestContext requestContext;

  @Autowired
  public AuthenticationAspect(
      AuthenticationUseCase authenticationUseCase,
      LoadUserUseCase loadUserUseCase,
      RequestContext requestContext) {
    this.authenticationUseCase = authenticationUseCase;
    this.loadUserUseCase = loadUserUseCase;
    this.requestContext = requestContext;
  }

  /** Authenticates incoming requests annotated with @Authenticated. */
  @Before("@annotation(com.gymcrm.configuration.security.Authenticated)")
  public void authenticateRequest() {
    authenticateAndLoadUser();
  }

  /** Checks permissions for methods annotated with @RequiresPermission. */
  @Before("@annotation(RequiresPermission)")
  public void checkPermissions(org.aspectj.lang.JoinPoint joinPoint) {
    User user = authenticateAndLoadUser();

    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();

    RequiresPermission requiresPermission = method.getAnnotation(RequiresPermission.class);
    if (requiresPermission == null) {
      return;
    }

    String[] requiredPermissions = requiresPermission.value();

    boolean hasPermission =
        Arrays.stream(requiredPermissions)
            .allMatch(permission -> roleHasPermission(user.getUserType(), permission));

    if (!hasPermission) {
      throw new ForbiddenException(
          formattedRole(user)
              + " does not have required permissions: "
              + Arrays.toString(requiredPermissions));
    }
  }

  /** Authenticates the request, loads the user, and updates the request context. */
  private User authenticateAndLoadUser() {
    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

    if (attributes == null) {
      throw new UnauthorizedException("Unable to retrieve request attributes.");
    }

    HttpServletRequest request = attributes.getRequest();
    String username = request.getHeader("authUsername");
    String password = request.getHeader("authPassword");

    if (username == null || password == null) {
      throw new UnauthorizedException("Missing username or password in headers.");
    }

    authenticationUseCase.authenticate(username, password);
    requestContext.setUsername(username);

    return loadUserUseCase.loadUserByUsername(username);
  }

  /** Checks if a given role has the required permission. */
  private boolean roleHasPermission(UserType role, String permission) {
    return PermissionConfig.ROLE_PERMISSIONS.getOrDefault(role, Set.of()).contains(permission);
  }

  private String formattedRole(User user) {
    String roleName = user.getUserType().name();
    return roleName.substring(0, 1).toUpperCase() + roleName.substring(1).toLowerCase();
  }
}
