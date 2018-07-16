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

  public List<String> updateValidate(Category category) {
    List<String> validationResults = validate(category);

    if (category.getParentCategory() != null &&
        !categoryService
            .canBeParentCategory(category.getId(), category.getParentCategory().getId())) {
      validationResults.add(Messages.PROVIDED_PARENT_CATEGORY_PROBLEM);
    }

    return validationResults;
  }

  public List<String> validate(Category category) {
    List<String> validationResults = new ArrayList<>();
    if (category.getName() == null || category.getName().equals("")) {
      validationResults.add(Messages.EMPTY_CATEGORY_NAME);
    } else if (categoryService.nameExist(category.getName())) {
      validationResults.add(Messages.PROVIDED_NAME_ALREADY_EXIST);
    }

    if (category.getParentCategory() != null &&
        !categoryService.idExist(category.getParentCategory().getId())) {
      validationResults.add(Messages.PROVIDED_PARRENT_CATEGORY_NOT_EXIST);
    }

    return validationResults;
  }

}