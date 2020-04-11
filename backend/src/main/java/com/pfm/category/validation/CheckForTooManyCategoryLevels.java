package com.pfm.category.validation;

import static com.pfm.config.MessagesProvider.TOO_MANY_CATEGORY_LEVELS;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckForTooManyCategoryLevelsValidator.class)
public @interface CheckForTooManyCategoryLevels {

  String message() default TOO_MANY_CATEGORY_LEVELS;

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
