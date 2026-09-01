package io.github.khaledabushamat.discount.catalog.domain;

import java.util.Collection;
import java.util.Map;

public interface ProductCatalog {
    Map<String, Product> findAllById(Collection<String> ids);
}