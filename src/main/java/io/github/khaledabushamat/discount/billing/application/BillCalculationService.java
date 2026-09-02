package io.github.khaledabushamat.discount.billing.application;

import io.github.khaledabushamat.discount.billing.domain.Bill;
import io.github.khaledabushamat.discount.billing.domain.DiscountBreakdown;
import io.github.khaledabushamat.discount.billing.domain.DiscountEngine;
import io.github.khaledabushamat.discount.billing.domain.LineItem;
import io.github.khaledabushamat.discount.catalog.domain.Product;
import io.github.khaledabushamat.discount.catalog.domain.ProductCatalog;
import io.github.khaledabushamat.discount.customer.domain.Customer;
import io.github.khaledabushamat.discount.customer.domain.CustomerRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class BillCalculationService {

    private final CustomerRepository customers;
    private final ProductCatalog catalog;
    private final DiscountEngine engine;
    private final Clock clock;

    public BillCalculationService(CustomerRepository customers,
                                  ProductCatalog catalog,
                                  DiscountEngine engine,
                                  Clock clock) {
        this.customers = customers;
        this.catalog = catalog;
        this.engine = engine;
        this.clock = clock;
    }

    public DiscountBreakdown calculate(String customerExternalId, List<LineItemRequest> lines) {
        Customer customer = customers.findByExternalId(customerExternalId)
                .orElseThrow(() -> new CustomerNotFoundException(customerExternalId));

        Map<String, Product> products = catalog.findAllById(
                lines.stream().map(LineItemRequest::productId).toList());

        List<LineItem> items = lines.stream()
                .map(line -> toLineItem(line, products))
                .toList();

        Bill bill = new Bill(customer, items, LocalDate.now(clock));
        return engine.calculate(bill);
    }

    private LineItem toLineItem(LineItemRequest line, Map<String, Product> products) {
        Product product = products.get(line.productId());
        if (product == null) {
            throw new ProductNotFoundException(line.productId());
        }
        return new LineItem(
                product.id(),
                product.category(),
                product.unitPrice(),
                line.quantity());
    }
}