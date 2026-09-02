package io.github.khaledabushamat.discount.catalog.infrastructure.mongo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import io.github.khaledabushamat.discount.TestcontainersConfiguration;
import io.github.khaledabushamat.discount.catalog.domain.Category;
import io.github.khaledabushamat.discount.catalog.domain.Product;
import io.github.khaledabushamat.discount.catalog.domain.ProductCatalog;
import io.github.khaledabushamat.discount.shared.Money;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class MongoProductCatalogTest {

    @Autowired
    private ProductCatalog catalog;

    @Test
    void seedsProductsOnStartup() {
        Map<String, Product> products = catalog.findAllById(List.of("laptop-01", "coffee-01"));

        assertThat(products).hasSize(2);
    }

    @Test
    void mapsPriceAndCategoryFromMongo() {
        Product laptop = catalog.findAllById(List.of("laptop-01")).get("laptop-01");

        assertThat(laptop.name()).isEqualTo("Laptop");
        assertThat(laptop.category()).isEqualTo(Category.NON_GROCERY);
        assertThat(laptop.unitPrice()).isEqualTo(Money.of("890.00"));
    }

    @Test
    void preservesDecimalPrecision() {
        Product headphones = catalog.findAllById(List.of("headphones-01")).get("headphones-01");

        assertThat(headphones.unitPrice()).isEqualTo(Money.of("199.99"));
    }

    @Test
    void identifiesGroceries() {
        Product coffee = catalog.findAllById(List.of("coffee-01")).get("coffee-01");

        assertThat(coffee.category()).isEqualTo(Category.GROCERY);
    }

    @Test
    void returnsOnlyProductsThatExist() {
        Map<String, Product> products = catalog.findAllById(List.of("laptop-01", "does-not-exist"));

        assertThat(products).containsOnlyKeys("laptop-01");
    }

    @Test
    void returnsEmptyMapForUnknownProducts() {
        assertThat(catalog.findAllById(List.of("ghost-01"))).isEmpty();
    }
}
