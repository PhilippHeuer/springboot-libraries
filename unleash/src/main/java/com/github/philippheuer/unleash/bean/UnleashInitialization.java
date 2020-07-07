package com.github.philippheuer.unleash.bean;

import com.github.philippheuer.events4j.core.EventManager;
import lombok.extern.slf4j.Slf4j;
import no.finn.unleash.DefaultUnleash;
import no.finn.unleash.FakeUnleash;
import no.finn.unleash.Unleash;
import no.finn.unleash.event.UnleashReady;
import no.finn.unleash.event.UnleashSubscriber;
import no.finn.unleash.repository.FeatureToggleResponse;
import no.finn.unleash.repository.ToggleCollection;
import no.finn.unleash.strategy.Strategy;
import no.finn.unleash.util.UnleashConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class UnleashInitialization {

    @Value("${unleash.appName}")
    private String appName;

    @Value("${unleash.instanceId:primary}")
    private String instanceId;

    @Value("${unleash.environment:prod}")
    private String environment;

    @Value("${unleash.api:}")
    private String api;

    @Value("${unleash.syncOnInit:true}")
    private Boolean synchronousFetchOnInitialisation = true;

    @Value("${unleash.fake:false}")
    private Boolean useFakeUnleash;

    @Value("${unleash.fetchTogglesInterval:5}")
    private Integer fetchTogglesInterval;

    @Value("${unleash.sendMetricsInterval:10}")
    private Integer sendMetricsInterval;

    @Autowired(required = false)
    private List<Strategy> customStrategies;

    @Autowired(required = false)
    private EventManager eventManager;

    @Bean
    public Unleash getUnleash() {
        // fake unleash can be enabled for local testing
        if (useFakeUnleash) {
            FakeUnleash fakeUnleash = new FakeUnleash();
            fakeUnleash.enableAll();
            return fakeUnleash;
        }

        // unleash
        UnleashConfig unleashConfig = UnleashConfig.builder()
                .appName(appName)
                .instanceId(instanceId)
                .environment(environment)
                .unleashAPI(api)
                .synchronousFetchOnInitialisation(synchronousFetchOnInitialisation)
                .fetchTogglesInterval(fetchTogglesInterval)
                .sendMetricsInterval(sendMetricsInterval)
                .subscriber(new UnleashSubscriber() {
                    @Override
                    public void onReady(UnleashReady ready) {
                        log.debug("Unleash is ready");

                        if (eventManager != null) {
                            eventManager.publish(ready);
                        }
                    }
                    @Override
                    public void togglesFetched(FeatureToggleResponse toggleResponse) {
                        log.debug("Fetched toggles! [Result: {}]", toggleResponse.getStatus());

                        if (eventManager != null) {
                            eventManager.publish(toggleResponse);
                        }
                    }

                    @Override
                    public void togglesBackedUp(ToggleCollection toggleCollection) {
                        log.debug("Stored a backup of the feature toggle state locally!");

                        if (eventManager != null) {
                            eventManager.publish(toggleCollection);
                        }
                    }
                })
                .build();

        if (customStrategies != null && customStrategies.size() > 0) {
            return new DefaultUnleash(unleashConfig, customStrategies.toArray(new Strategy[0]));
        }

        return new DefaultUnleash(unleashConfig);
    }

}
