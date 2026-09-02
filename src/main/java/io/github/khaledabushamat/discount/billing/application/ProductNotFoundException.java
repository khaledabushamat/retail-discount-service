package io.github.khaledabushamat.discount.billing.application;

public class ProductNotFoundException extends RuntimeException {

    private final String productId;

    public ProductNotFoundException(String productId) {
        super("Unknown product: " + productId);
        this.productId = productId;
    }

    public String getProductId() {
        return productId;
    }
}
