package io.github.khaledabushamat.discount.catalog.domain;

import io.github.khaledabushamat.discount.shared.Money;

public record Product(String id, String name, Category category, Money unitPrice) {}
