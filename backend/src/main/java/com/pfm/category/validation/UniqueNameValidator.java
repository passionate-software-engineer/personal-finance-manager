package com.pfm.category.validation;

import com.pfm.auth.UserProvider;
import com.pfm.category.CategoryRepository;
import com.pfm.category.requests.RequestType;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class UniqueNameValidator implements ConstraintValidator<UniqueName, String> {

  private final CategoryRepository categoryRepository;
  private final UserProvider userProvider;
  private RequestType requestType;

  @Autowired
  public UniqueNameValidator(CategoryRepository categoryRepository, UserProvider userProvider) {
    this.categoryRepository = categoryRepository;
    this.userProvider = userProvider;
  }

  @Override
  public boolean isValid(String name, ConstraintValidatorContext context) {
    if (requestType == RequestType.ADD) {
      return categoryRepository.findByNameIgnoreCaseAndUserId(name, userProvider.getCurrentUserId()).size() == 0;
    } else {
      return true;
    }
  }

  @Override
  public void initialize(UniqueName constraintAnnotation) {
    this.requestType = constraintAnnotation.requestType();

  }
}
