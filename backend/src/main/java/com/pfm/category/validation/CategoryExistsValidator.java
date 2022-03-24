package com.pfm.category.validation;

import com.pfm.auth.UserProvider;
import com.pfm.category.CategoryRepository;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CategoryExistsValidator implements ConstraintValidator<CategoryExistsIfProvided, Long> {

  private final CategoryRepository categoryRepository;
  private final UserProvider userProvider;

  @Override
  public boolean isValid(Long categoryId, ConstraintValidatorContext context) {
    return categoryId == null || categoryRepository.existsByIdAndUserId(categoryId, userProvider.getCurrentUserId());
  }

  @Override
  public void initialize(CategoryExistsIfProvided constraintAnnotation) {
  }
}
