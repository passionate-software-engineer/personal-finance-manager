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

  public List<String> validate(Category category) {
    List<String> validationResults = new ArrayList<>();
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


