package io.github.khaledabushamat.discount.catalog.infrastructure.mongo;

import io.github.khaledabushamat.discount.catalog.domain.Category;
import io.github.khaledabushamat.discount.catalog.domain.Product;
import io.github.khaledabushamat.discount.catalog.domain.ProductCatalog;
import io.github.khaledabushamat.discount.shared.Money;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toMap;

@Component
class MongoProductCatalog implements ProductCatalog {

    private final ProductMongoRepository repository;

    MongoProductCatalog(ProductMongoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Map<String, Product> findAllById(Collection<String> ids) {
        return repository.findAllById(ids).stream()
                .map(this::toDomain)
                .collect(toMap(Product::id, identity()));
    }

    private Product toDomain(ProductDocument document) {
        return new Product(
                document.getId(),
                document.getName(),
                document.getCategory(),
                Money.of(document.getUnitPrice()));
    }
}
