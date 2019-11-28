package com.github.philippheuer.springboot.auth.jwt.camunda;

import com.github.philippheuer.common.exceptions.AccessDeniedException;
import com.github.philippheuer.springboot.auth.jwt.domain.JWTAuthenticationToken;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.rest.util.EngineUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Camunda Engine Authentication
 */
public class ProcessEngineJWTAuthenticationFilter implements Filter {

    private static Logger log = LoggerFactory.getLogger("ProcessEngineJWTAuthenticationFilter");

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ProcessEngine engine = EngineUtil.lookupProcessEngine("default");
        final HttpServletRequest req = (HttpServletRequest) request;

        // Extract authentication details
        JWTAuthenticationToken authentication = (JWTAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            clearAuthentication(engine);
            throw new AccessDeniedException("AUTH-1000", "Access denied, no valid access token received!");
        }

        // Authentication group memberships
        List<String> groupIds = new ArrayList<>();
        authentication.getAuthorities().forEach(authority -> {
            groupIds.add(authority.getAuthority());
        });

        try {
            engine.getIdentityService().setAuthentication(authentication.getUserId(), groupIds);
            chain.doFilter(request, response);
        } finally {
            clearAuthentication(engine);
        }
    }

    @Override
    public void destroy() {

    }

    private void clearAuthentication(ProcessEngine engine) {
        engine.getIdentityService().clearAuthentication();
    }
}
