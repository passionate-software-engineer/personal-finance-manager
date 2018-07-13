package com.pfm.category;

import com.pfm.Messages;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class CategoryValidator {

  private CategoryService categoryService;

  public List<String> addCategoryValidation(Category category) {
    List<String> validationResults = new ArrayList<>();
    if (category.getId() != null && categoryService.idExist(category.getId())) {
      validationResults.add(Messages.ADD_CATEGORY_PROVIDED_ID_ALREADY_EXIST);
    }
    return validate(validationResults, category);
  }

  public List<String> updateCategoryValidation(Category category) {
    List<String> validationResults = new ArrayList<>();
    return validate(validationResults, category);
  }


  private List<String> validate(List<String> validationResults, Category category) {
    if (category.getName() == null || category.getName().equals("")) {
      validationResults.add(Messages.EMPTY_CATEGORY_NAME);
    }
    if (category.getParentCategory() != null &&
        !categoryService.idExist(category.getParentCategory().getId())) {
      validationResults.add(Messages.PROVIDED_PARRENT_CATEGORY_NOT_EXIST);
    }
    return validationResults;
  }

}


