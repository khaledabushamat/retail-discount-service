package io.github.khaledabushamat.discount.shared;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {
        "io.github.khaledabushamat.discount.billing.infrastructure.jpa",
        "io.github.khaledabushamat.discount.customer.infrastructure.jpa"
})
@EnableMongoRepositories(basePackages =
        "io.github.khaledabushamat.discount.catalog.infrastructure.mongo")
public class PersistenceConfig {
}