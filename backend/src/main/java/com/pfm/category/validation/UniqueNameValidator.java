package com.pfm.category.validation;

import com.pfm.auth.UserProvider;
import com.pfm.category.CategoryRepository;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class UniqueNameValidator implements ConstraintValidator<UniqueName, String> {

  private final transient CategoryRepository categoryRepository;
  private final transient UserProvider userProvider;

  @Autowired
  public UniqueNameValidator(CategoryRepository categoryRepository, UserProvider userProvider) {
    this.categoryRepository = categoryRepository;
    this.userProvider = userProvider;
  }

  @Override
  public boolean isValid(String name, ConstraintValidatorContext context) {
    return categoryRepository.findByNameIgnoreCaseAndUserId(name, userProvider.getCurrentUserId()).size() == 0;
  }

  @Override
  public void initialize(UniqueName constraintAnnotation) {
  }
}
