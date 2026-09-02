package io.github.khaledabushamat.discount.shared.security;

import java.lang.annotation.*;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal(expression = "subject")
public @interface CurrentCustomerId {}
