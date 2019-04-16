package com.github.philippheuer.webfluxerror.domain;

import com.github.philippheuer.events4j.domain.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Gets triggered for each exception
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExceptionEvent extends Event {

    private final Throwable throwable;

    public ExceptionEvent(Throwable throwable) {
        this.throwable = throwable;
    }

}
