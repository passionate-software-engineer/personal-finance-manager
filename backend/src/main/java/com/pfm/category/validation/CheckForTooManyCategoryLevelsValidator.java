package com.pfm.category.validation;

import com.pfm.category.Category;
import com.pfm.category.CategoryRepository;
import java.util.Optional;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CheckForTooManyCategoryLevelsValidator implements ConstraintValidator<CheckForTooManyCategoryLevels, Long> {

  private final CategoryRepository categoryRepository;

  @Override
  public boolean isValid(Long categoryId, ConstraintValidatorContext context) {
    if (categoryId == null) {
      return true;
    }

    Optional<Category> categoryById = categoryRepository.findById(categoryId);
    return categoryById.isEmpty() || categoryById.get().getParentCategory() == null;
  }

  @Override
  public void initialize(CheckForTooManyCategoryLevels constraintAnnotation) {
  }
}
