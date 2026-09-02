package io.github.khaledabushamat.discount.billing.api;

import java.util.Map;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.github.khaledabushamat.discount.billing.application.CustomerNotFoundException;
import io.github.khaledabushamat.discount.billing.application.ProductNotFoundException;

@RestControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    ProblemDetail handleCustomerNotFound(CustomerNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Customer not found");
        return problem;
    }

    @ExceptionHandler(ProductNotFoundException.class)
    ProblemDetail handleProductNotFound(ProductNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setTitle("Product not found");
        problem.setProperty("productId", ex.getProductId());
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Invalid request");
        problem.setProperty(
                "errors",
                ex.getBindingResult().getFieldErrors().stream()
                        .map(error -> Map.of(
                                "field", error.getField(),
                                "message", Objects.requireNonNullElse(error.getDefaultMessage(), "invalid")))
                        .toList());
        return problem;
    }
}
