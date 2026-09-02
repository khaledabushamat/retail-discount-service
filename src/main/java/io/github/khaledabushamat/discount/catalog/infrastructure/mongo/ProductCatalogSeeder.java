package io.github.khaledabushamat.discount.catalog.infrastructure.mongo;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import io.github.khaledabushamat.discount.catalog.domain.Category;

@Profile("!prod")
@Component
class ProductCatalogSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ProductCatalogSeeder.class);

    private final ProductMongoRepository repository;

    ProductCatalogSeeder(ProductMongoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        long existing = repository.count();
        log.info("Product catalog contains {} documents", existing);
        if (existing > 0) {
            return;
        }

        repository.saveAll(List.of(
                product("laptop-01", "Laptop", Category.NON_GROCERY, "890.00"),
                product("phone-01", "Smartphone", Category.NON_GROCERY, "650.00"),
                product("headphones-01", "Headphones", Category.NON_GROCERY, "199.99"),
                product("rice-01", "Rice 5kg", Category.GROCERY, "40.00"),
                product("milk-01", "Milk 1L", Category.GROCERY, "3.50"),
                product("coffee-01", "Coffee 500g", Category.GROCERY, "100.00")));
        log.info("Seeded {} products", repository.count());
    }

    private ProductDocument product(String id, String name, Category category, String price) {
        return new ProductDocument(id, name, category, new BigDecimal(price));
    }
}
