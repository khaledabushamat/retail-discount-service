package io.github.khaledabushamat.discount.catalog.infrastructure.mongo;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import io.github.khaledabushamat.discount.catalog.domain.Category;

@Document(collection = "products")
class ProductDocument {

    @Id
    private String id;

    private String name;

    private Category category;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal unitPrice;

    protected ProductDocument() {}

    ProductDocument(String id, String name, Category category, BigDecimal unitPrice) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.unitPrice = unitPrice;
    }

    String getId() {
        return id;
    }

    String getName() {
        return name;
    }

    Category getCategory() {
        return category;
    }

    BigDecimal getUnitPrice() {
        return unitPrice;
    }
}
