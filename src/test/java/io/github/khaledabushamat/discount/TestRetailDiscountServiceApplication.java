package io.github.khaledabushamat.discount;

import org.springframework.boot.SpringApplication;

public class TestRetailDiscountServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(RetailDiscountServiceApplication::main)
                .with(TestcontainersConfiguration.class)
                .run(args);
    }
}
