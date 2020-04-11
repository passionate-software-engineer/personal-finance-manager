package com.pfm.category.validation;

import static com.pfm.config.MessagesProvider.PROVIDED_PARENT_CATEGORY_DOES_NOT_EXIST;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CategoryExistsValidator.class)
public @interface CategoryExistsIfProvided {

  String message() default PROVIDED_PARENT_CATEGORY_DOES_NOT_EXIST;

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
