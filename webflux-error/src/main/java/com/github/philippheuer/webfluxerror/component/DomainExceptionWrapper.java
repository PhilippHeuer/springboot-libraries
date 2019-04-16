package com.github.philippheuer.webfluxerror.component;

import com.github.philippheuer.events4j.EventManager;
import com.github.philippheuer.webfluxerror.domain.ExceptionEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Slf4j
class DomainExceptionWrapper extends DefaultErrorAttributes {

    @Autowired
    private EventManager eventManager;

    /**
     * Constructor
     */
    public DomainExceptionWrapper() {
        super(true);
    }

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
        final Throwable error = getError(request);
        final Map<String, Object> errorAttributes = super.getErrorAttributes(request, true);
        log.trace("ErrorAttributes: Starting with the following context: " + errorAttributes);

        // generate event for the exception (can be reported to user-defined services)
        eventManager.dispatchEvent(new ExceptionEvent(error));

        // prepare new error attributes
        Map<String, Object> errorResponse = new LinkedHashMap<>();
        Map<String, Object> errorContext = new LinkedHashMap<>();
        // - error context
        errorContext.put("type", error.getClass().getSimpleName());
        if (error instanceof ContextedRuntimeException) {
            ContextedRuntimeException cre = (ContextedRuntimeException) error;
            cre.getContextEntries().forEach(entry -> {
                errorContext.put(entry.getKey(), entry.getValue());
            });
        }
        if (error instanceof BindException) {
            BindException be = (BindException) error;
            errorContext.put("errors", be.getAllErrors());
        }
        // - error schema
        errorResponse.put("status", errorAttributes.get("status")); // http status
        errorResponse.put("code", errorAttributes.getOrDefault("code", "UNKNOWN")); // error code
        errorResponse.put("message", error.getMessage()); // error message
        errorResponse.put("context", errorContext); // error context

        log.trace("ErrorAttributes: Finishing with the following context: " + errorResponse);
        return errorResponse;
    }

}
