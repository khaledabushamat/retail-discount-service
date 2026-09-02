package io.github.khaledabushamat.discount.billing.application;

public class CustomerNotFoundException extends RuntimeException {

    private final String externalId;

    public CustomerNotFoundException(String externalId) {
        super("Unknown customer: " + externalId);
        this.externalId = externalId;
    }

    public String getExternalId() {
        return externalId;
    }
}