package com.github.philippheuer.springboot.error.handler;

import com.github.philippheuer.common.exceptions.AbstractBaseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ContextedException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * API Exception Handler (JSON)
 */
@ControllerAdvice
@ConditionalOnClass(org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler.class)
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    protected ResponseEntity<Object> handleConflict(Exception ex, WebRequest request, HttpServletResponse response) {
        // Parse Exceptions
        Integer httpErrorCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        String errorType = ex.getClass().getSimpleName().replace("Exception", "");
        String errorCode = "1";
        String errorMessage = "Unknown Error";
        Map<String, Object> errorContext = new HashMap<>();

        if (ex instanceof AbstractBaseException) {
            AbstractBaseException aex = (AbstractBaseException) ex;
            httpErrorCode = aex.getHttpStatusCode();
            errorCode = aex.getErrorCode();
            errorMessage = aex.getErrorMessage();

            aex.getContextEntries().forEach(entry -> errorContext.put(entry.getKey(), entry.getValue()));
        } else if (ex instanceof ContextedException) {
            ContextedException cex = (ContextedException) ex;
            errorMessage = cex.getRawMessage();

            cex.getContextEntries().forEach(entry -> errorContext.put(entry.getKey(), entry.getValue()));
        } else {
            errorMessage = ex.getMessage();
        }

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        // Body
        Map<String, Object> responseBody = new HashMap<String, Object>();

        // errorAttributeMap also supports path, trace & timestamp
        responseBody.put("errorCode", errorCode);
        responseBody.put("errorType", errorType);
        responseBody.put("errorMessage", errorMessage);
        responseBody.put("errorContext", errorContext);

        // log error
        log.error("{}: {} - {}", errorType, errorCode, errorMessage);

        // print stack trace for unexpected exceptions
        if (ex instanceof AbstractBaseException == false) {
            ex.printStackTrace();
        }

        // respond to client
        return handleExceptionInternal(ex, responseBody, headers, HttpStatus.valueOf(httpErrorCode), request);
    }
}
