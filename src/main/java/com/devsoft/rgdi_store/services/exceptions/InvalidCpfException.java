package com.devsoft.rgdi_store.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CpfValidator.class)
public @interface ValidCPF {
    String message() default "{com.devsoft.rgdi_store.dto.ValidCPF.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
