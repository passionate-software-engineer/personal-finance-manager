package com.pfm.category;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class CategoryService {

  private CategoryRepository categoryRepository;

  public Optional<Category> getCategoryByIdAndUserId(long id, long userId) {
    return categoryRepository.findByIdAndUserId(id, userId);
  }

  public Category getCategoryFromDbByIdAndUserId(long id, long userId) {
    Optional<Category> categoryByIdAndUserId = getCategoryByIdAndUserId(id, userId);
    if (categoryByIdAndUserId.isEmpty()) {
      throw new IllegalStateException("CATEGORY with id : " + id + " does not exist in database");
    }

    return categoryByIdAndUserId.get();
  }

  public List<Category> getCategories(long userId) {
    return categoryRepository.findByUserId(userId).stream()
        .sorted(Comparator.comparing(Category::getId))
        .collect(Collectors.toList());
  }

  public Category addCategory(Category category, long userId) {
    category.setUserId(userId);
    if (category.getParentCategory() == null) {
      return categoryRepository.save(category);
    }

    Category parentCategory = getCategoryByIdAndUserId(category.getParentCategory().getId(), userId)
        .orElseThrow(() -> new IllegalStateException("Cannot find parent category with id " + category.getParentCategory().getId()));
    checkForTooManyCategoryLevels(parentCategory);
    category.setParentCategory(parentCategory);

    return categoryRepository.save(category);
  }

  public void deleteCategory(long id) {
    categoryRepository.deleteById(id);
  }

  public void updateCategory(long id, long userId, Category category) {
    Optional<Category> receivedCategory = getCategoryByIdAndUserId(id, userId);
    if (receivedCategory.isEmpty()) {
      throw new IllegalStateException("CATEGORY with id : " + id + " does not exist in database");
    }

    Category categoryToUpdate = receivedCategory.get();
    categoryToUpdate.setName(category.getName());
    categoryToUpdate.setPriority(category.getPriority());

    if (category.getParentCategory() == null) {
      categoryToUpdate.setParentCategory(null);
    } else {
      Optional<Category> parentCategory = getCategoryByIdAndUserId(category.getParentCategory().getId(), userId);
      if (parentCategory.isEmpty()) {
        throw new IllegalStateException("CATEGORY with id : " + category.getParentCategory().getId()
            + " does not exist in database");
      }
      Category parentCategoryAfterUpdate = parentCategory.get();
      checkForTooManyCategoryLevels(parentCategoryAfterUpdate);
      categoryToUpdate.setParentCategory(parentCategoryAfterUpdate);
    }

    categoryRepository.save(categoryToUpdate);
  }

  private void checkForTooManyCategoryLevels(Category parentCategory) {
    if (parentCategory.getParentCategory() != null) {
      throw new IllegalStateException("Too many category levels.");
    }
  }

  public boolean isParentCategory(long id) {
    return categoryRepository.numberOfEntriesUsingThisCategoryAsParentId(id) != 0;
  }

  public boolean idExist(long id) {
    return categoryRepository.existsById(id);
  }

  public boolean isCategoryNameAlreadyUsed(String name, long userId) {
    return categoryRepository.findByNameIgnoreCaseAndUserId(name, userId).size() != 0;
  }

  public boolean categoryExistByIdAndUserId(long categoryId, long userId) {
    return categoryRepository.existsByIdAndUserId(categoryId, userId);
  }

}
