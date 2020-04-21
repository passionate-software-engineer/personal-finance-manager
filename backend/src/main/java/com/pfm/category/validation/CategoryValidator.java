package com.pfm.category.validation;

import static com.pfm.config.MessagesProvider.CATEGORIES_CYCLE_DETECTED;
import static com.pfm.config.MessagesProvider.CATEGORY_IS_USED_IN_FILTER;
import static com.pfm.config.MessagesProvider.CATEGORY_IS_USED_IN_TRANSACTION;
import static com.pfm.config.MessagesProvider.CATEGORY_WITH_PROVIDED_NAME_ALREADY_EXISTS;
import static com.pfm.config.MessagesProvider.getMessage;

import com.pfm.category.Category;
import com.pfm.category.CategoryService;
import com.pfm.filter.FilterService;
import com.pfm.transaction.TransactionService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CategoryValidator {

  private final CategoryService categoryService;
  private final FilterService filterService;
  private final TransactionService transactionService;

  public List<String> validateCategoryForUpdate(long id, long userId, Category category) {
    List<String> validationResults = new ArrayList<>();

    Optional<Category> categoryToUpdate = categoryService.getCategoryByIdAndUserId(id, userId);

    if (categoryToUpdate.isEmpty()) {
      throw new IllegalStateException("Category with id: " + id + " does not exist in database");
    }

    if (!categoryToUpdate.get().getName().equals(category.getName())) {
      checkForDuplicatedName(validationResults, category, userId);
    }

    if (category.getParentCategory() != null && id == category.getParentCategory().getId()) {
      validationResults.add(getMessage(CATEGORIES_CYCLE_DETECTED));
    }

    return validationResults;
  }

  private void checkForDuplicatedName(List<String> validationResults, Category category, long userId) {
    if (categoryService.isCategoryNameAlreadyUsed(category.getName(), userId)) {
      validationResults.add(getMessage(CATEGORY_WITH_PROVIDED_NAME_ALREADY_EXISTS));
    }
  }

  public List<String> validateCategoryForDelete(long categoryId) {
    List<String> validationErrors = new ArrayList<>();

    if (transactionService.transactionExistByCategoryId(categoryId)) {
      validationErrors.add(getMessage(CATEGORY_IS_USED_IN_TRANSACTION));
    }

    if (filterService.filterExistByCategoryId(categoryId)) {
      validationErrors.add(getMessage(CATEGORY_IS_USED_IN_FILTER));
    }

    return validationErrors;
  }
}
