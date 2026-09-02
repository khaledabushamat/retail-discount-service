package io.github.khaledabushamat.discount.billing.application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
class BillingConfiguration {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}