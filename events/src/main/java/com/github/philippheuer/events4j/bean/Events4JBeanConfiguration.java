package com.github.philippheuer.events4j.bean;

import com.github.philippheuer.events4j.core.EventManager;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Register the Events4J Bean
 */
@Component
public class Events4JBeanConfiguration {

    @Bean
    private EventManager getEventManager() {
        EventManager eventManager = new EventManager();
        eventManager.autoDiscovery();
        return eventManager;
    }

}
