package com.github.philippheuer.error.reporting.service;

import io.sentry.SentryClient;
import io.sentry.event.BreadcrumbBuilder;
import io.sentry.event.UserBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@ConditionalOnBean(SentryClient.class)
public class ErrorReportingService {

    @Autowired
    private SentryClient sentryClient;

    /**
     * Handle the exception
     *
     * @param ex Exception
     */
    public void handleException(Throwable ex) {
        sentryClient.sendException(ex);
    }

    /**
     * Records a action to trace the origin of the error
     *
     * @param actionMessage Action Message
     */
    public void recordAction(String actionMessage) {
        sentryClient.getContext().recordBreadcrumb(
            new BreadcrumbBuilder().setMessage(actionMessage).build()
        );
    }

    /**
     * Adds a tag
     *
     * @param name Name
     * @param value Value
     */
    public void addTag(String name, String value) {
        sentryClient.getContext().addTag(name, value);
    }

    /**
     * Sets the user context
     *
     * @param userId  User Id
     * @param username Username
     * @param data Data
     */
    public void setUserContext(String userId, String username, Map<String, Object> data) {
        // Set the current user in the context.
        sentryClient.getContext().setUser(
            new UserBuilder()
                .setId(userId)
                .setUsername(username)
                .setData(data)
                .build()
        );
    }

    /**
     * Clear User Context
     */
    public void clearUserContext() {
        sentryClient.getContext().setUser(null);
    }

    /**
     * Clear the Context
     */
    public void clearContext() {
        sentryClient.getContext().clear();
    }

}
