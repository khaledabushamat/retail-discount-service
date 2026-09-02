package io.github.khaledabushamat.discount.billing.application;

import io.github.khaledabushamat.discount.billing.domain.Bill;
import io.github.khaledabushamat.discount.billing.domain.DiscountBreakdown;
import io.github.khaledabushamat.discount.billing.domain.DiscountEngine;
import io.github.khaledabushamat.discount.billing.domain.LineItem;
import io.github.khaledabushamat.discount.catalog.domain.Category;
import io.github.khaledabushamat.discount.catalog.domain.Product;
import io.github.khaledabushamat.discount.catalog.domain.ProductCatalog;
import io.github.khaledabushamat.discount.customer.domain.Customer;
import io.github.khaledabushamat.discount.customer.domain.CustomerRepository;
import io.github.khaledabushamat.discount.customer.domain.CustomerType;
import io.github.khaledabushamat.discount.shared.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillCalculationServiceTest {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);

    @Mock
    private CustomerRepository customers;

    @Mock
    private ProductCatalog catalog;

    @Mock
    private DiscountEngine engine;

    private BillCalculationService service;

    @BeforeEach
    void setUp() {
        service = new BillCalculationService(customers, catalog, engine, FIXED_CLOCK);
    }

    @Test
    void resolvesPricesFromTheCatalogNotTheRequest() {
        givenCustomer("emp-001", CustomerType.EMPLOYEE);
        givenProduct("laptop-01", Category.NON_GROCERY, "890.00");
        when(engine.calculate(any())).thenReturn(anyBreakdown());

        service.calculate("emp-001", List.of(new LineItemRequest("laptop-01", 1)));

        ArgumentCaptor<Bill> captor = ArgumentCaptor.forClass(Bill.class);
        verify(engine).calculate(captor.capture());

        LineItem item = captor.getValue().items().getFirst();
        assertThat(item.unitPrice()).isEqualTo(Money.of("890.00"));
        assertThat(item.category()).isEqualTo(Category.NON_GROCERY);
    }

    @Test
    void stampsTheBillWithTheCurrentDate() {
        givenCustomer("emp-001");
        givenProduct("laptop-01", Category.NON_GROCERY, "100.00");
        when(engine.calculate(any())).thenReturn(anyBreakdown());

        service.calculate("emp-001", List.of(new LineItemRequest("laptop-01", 1)));

        ArgumentCaptor<Bill> captor = ArgumentCaptor.forClass(Bill.class);
        verify(engine).calculate(captor.capture());

        assertThat(captor.getValue().billedOn()).isEqualTo(LocalDate.of(2026, 1, 1));
    }

    @Test
    void looksUpAllProductsInOneCall() {
        givenCustomer("emp-001");
        when(catalog.findAllById(any())).thenReturn(Map.of(
                "laptop-01", product("laptop-01", Category.NON_GROCERY, "890.00"),
                "milk-01", product("milk-01", Category.GROCERY, "3.50")));
        when(engine.calculate(any())).thenReturn(anyBreakdown());

        service.calculate("emp-001", List.of(
                new LineItemRequest("laptop-01", 1),
                new LineItemRequest("milk-01", 2)));

        verify(catalog, times(1)).findAllById(any());
    }

    @Test
    void failsWhenCustomerIsUnknown() {
        when(customers.findByExternalId("nobody")).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.calculate("nobody", List.of(new LineItemRequest("laptop-01", 1))))
                .isInstanceOf(CustomerNotFoundException.class);

        verifyNoInteractions(engine);
    }

    @Test
    void failsWhenProductIsUnknown() {
        givenCustomer("emp-001");
        when(catalog.findAllById(any())).thenReturn(Map.of());

        assertThatThrownBy(() ->
                service.calculate("emp-001", List.of(new LineItemRequest("ghost-01", 1))))
                .isInstanceOf(ProductNotFoundException.class);

        verifyNoInteractions(engine);
    }

    private void givenCustomer(String externalId, CustomerType... types) {
        when(customers.findByExternalId(externalId)).thenReturn(Optional.of(
                new Customer(externalId, LocalDate.of(2025, 6, 1), Set.of(types))));
    }

    private void givenProduct(String id, Category category, String price) {
        when(catalog.findAllById(any())).thenReturn(
                Map.of(id, product(id, category, price)));
    }

    private static Product product(String id, Category category, String price) {
        return new Product(id, id, category, Money.of(price));
    }

    private static DiscountBreakdown anyBreakdown() {
        return new DiscountBreakdown(Money.ZERO, Money.ZERO, Money.ZERO);
    }
}