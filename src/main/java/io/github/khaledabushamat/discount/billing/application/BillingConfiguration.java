package io.github.khaledabushamat.discount.billing.application;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class BillingConfiguration {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
