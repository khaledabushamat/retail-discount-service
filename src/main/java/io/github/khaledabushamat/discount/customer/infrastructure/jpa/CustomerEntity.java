package io.github.khaledabushamat.discount.customer.infrastructure.jpa;

import io.github.khaledabushamat.discount.customer.domain.CustomerType;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "customers")
class CustomerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", nullable = false, unique = true, length = 64)
    private String externalId;

    @Column(name = "joined_at", nullable = false)
    private LocalDate joinedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "customer_types",
            joinColumns = @JoinColumn(name = "customer_id"))
    @Column(name = "type", nullable = false, length = 32)
    @Enumerated(EnumType.STRING)
    private Set<CustomerType> types = new HashSet<>();

    protected CustomerEntity() {
    }

    String getExternalId() {
        return externalId;
    }

    LocalDate getJoinedAt() {
        return joinedAt;
    }

    Set<CustomerType> getTypes() {
        return types;
    }
}