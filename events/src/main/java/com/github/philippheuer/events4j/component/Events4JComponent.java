package com.github.philippheuer.events4j.component;

import com.github.philippheuer.events4j.EventManager;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Register the Events4J Bean
 */
@Component
public class Events4JComponent {

    @Bean
    private EventManager getEventManager() {
        EventManager eventManager = new EventManager();
        return eventManager;
    }

}
