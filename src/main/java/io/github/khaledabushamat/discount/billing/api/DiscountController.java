package io.github.khaledabushamat.discount.billing.api;

import io.github.khaledabushamat.discount.billing.application.BillCalculationService;
import io.github.khaledabushamat.discount.billing.application.LineItemRequest;
import io.github.khaledabushamat.discount.shared.security.CurrentCustomerId;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/discounts")
class DiscountController {

    private final BillCalculationService service;

    DiscountController(BillCalculationService service) {
        this.service = service;
    }

    @PostMapping("/calculate")
    DiscountResponse calculate(@CurrentCustomerId String customerId,
                               @Valid @RequestBody CalculateDiscountRequest request) {

        List<LineItemRequest> lines = request.lines().stream()
                .map(line -> new LineItemRequest(line.productId(), line.quantity()))
                .toList();

        return DiscountResponse.from(service.calculate(customerId, lines));
    }
}