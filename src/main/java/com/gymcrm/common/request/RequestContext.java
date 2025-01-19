package com.gymcrm.common.request;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Data
@RequestScope
@Component
public class RequestContext {
	private String username;
}
