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
      validationResults.add("Category cant be self parent category and any of subcategories");
    }

    return validationResults;
  }

  public List<String> validate(Category category) {
    List<String> validationResults = new ArrayList<>();
    if (category.getName() == null || category.getName().equals("")) {
      validationResults.add(Messages.EMPTY_CATEGORY_NAME);
    }
    if (category.getParentCategory() != null &&
        !categoryService.idExist(category.getParentCategory().getId())) {
      validationResults.add(Messages.PROVIDED_PARRENT_CATEGORY_NOT_EXIST);
//      if (category.getParentCategory().getId().equals(category.getId())) {
//        validationResults.add("dupa");
//      }
    }

    return validationResults;
  }

}