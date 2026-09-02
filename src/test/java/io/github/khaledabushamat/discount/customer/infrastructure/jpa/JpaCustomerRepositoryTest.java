package io.github.khaledabushamat.discount.customer.infrastructure.jpa;

import io.github.khaledabushamat.discount.customer.domain.CustomerRepository;
import io.github.khaledabushamat.discount.customer.domain.CustomerType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class JpaCustomerRepositoryTest {

    @Autowired
    private CustomerRepository repository;

    @Test
    void loadsCustomerWithTypes() {
        var customer = repository.findByExternalId("emp-001").orElseThrow();

        assertThat(customer.joinedAt()).isEqualTo(LocalDate.of(2020, 1, 15));
        assertThat(customer.isOfType(CustomerType.EMPLOYEE)).isTrue();
    }

    @Test
    void returnsEmptyForUnknownCustomer() {
        assertThat(repository.findByExternalId("nobody")).isEmpty();
    }
}