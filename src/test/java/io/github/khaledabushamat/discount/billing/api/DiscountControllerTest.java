package io.github.khaledabushamat.discount.billing.api;

import io.github.khaledabushamat.discount.billing.application.*;
import io.github.khaledabushamat.discount.billing.domain.DiscountBreakdown;
import io.github.khaledabushamat.discount.shared.Money;
import io.github.khaledabushamat.discount.shared.security.JwtConfig;
import io.github.khaledabushamat.discount.shared.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DiscountController.class)
@Import({SecurityConfig.class, JwtConfig.class})
@TestPropertySource(properties = "app.security.jwt.secret=test-secret-at-least-32-bytes-long-ok")
class DiscountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BillCalculationService service;

    @Test
    void returnsTheCalculatedBreakdown() throws Exception {
        when(service.calculate(eq("emp-001"), any())).thenReturn(breakdown());

        mockMvc.perform(post("/api/v1/discounts/calculate")
                        .with(jwt().jwt(builder -> builder.subject("emp-001")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"lines":[{"productId":"laptop-01","quantity":1}]}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grossTotal").value(990.00))
                .andExpect(jsonPath("$.percentageDiscount").value(267.00))
                .andExpect(jsonPath("$.flatDiscount").value(45.00))
                .andExpect(jsonPath("$.netPayable").value(678.00));
    }

    @Test
    void rejectsUnauthenticatedRequests() throws Exception {
        mockMvc.perform(post("/api/v1/discounts/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"lines":[{"productId":"laptop-01","quantity":1}]}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsEmptyLineItems() throws Exception {
        mockMvc.perform(post("/api/v1/discounts/calculate")
                        .with(jwt().jwt(builder -> builder.subject("emp-001")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"lines":[]}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsNonPositiveQuantity() throws Exception {
        mockMvc.perform(post("/api/v1/discounts/calculate")
                        .with(jwt().jwt(builder -> builder.subject("emp-001")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"lines":[{"productId":"laptop-01","quantity":0}]}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void returnsNotFoundForUnknownCustomer() throws Exception {
        when(service.calculate(any(), any()))
                .thenThrow(new CustomerNotFoundException("ghost"));

        mockMvc.perform(post("/api/v1/discounts/calculate")
                        .with(jwt().jwt(builder -> builder.subject("ghost")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"lines":[{"productId":"laptop-01","quantity":1}]}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void returnsUnprocessableEntityForUnknownProduct() throws Exception {
        when(service.calculate(any(), any()))
                .thenThrow(new ProductNotFoundException("ghost-01"));

        mockMvc.perform(post("/api/v1/discounts/calculate")
                        .with(jwt().jwt(builder -> builder.subject("emp-001")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"lines":[{"productId":"ghost-01","quantity":1}]}
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.productId").value("ghost-01"));
    }

    private static DiscountBreakdown breakdown() {
        return new DiscountBreakdown(
                Money.of("990.00"), Money.of("267.00"), Money.of("45.00"));
    }
}