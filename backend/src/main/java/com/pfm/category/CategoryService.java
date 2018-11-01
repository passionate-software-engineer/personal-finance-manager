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
    category.setParentCategory(parentCategory);

    return categoryRepository.save(category);
  }

  public void deleteCategory(long id) {
    categoryRepository.deleteById(id);
  }

  public void updateCategory(long id, long userId, Category category) {
    Optional<Category> receivedCategory = getCategoryByIdAndUserId(id, userId);
    if (!receivedCategory.isPresent()) {
      throw new IllegalStateException("Category with id : " + id + " does not exist in database");
    }

    Category categoryToUpdate = receivedCategory.get();
    categoryToUpdate.setName(category.getName());

    if (category.getParentCategory() == null) {
      categoryToUpdate.setParentCategory(null);
    } else {
      Optional<Category> parentCategory = getCategoryByIdAndUserId(category.getParentCategory().getId(), userId);
      if (!parentCategory.isPresent()) {
        throw new IllegalStateException("Category with id : " + category.getParentCategory().getId()
            + " does not exist in database");
      }
      categoryToUpdate.setParentCategory(parentCategory.get());
    }

    categoryRepository.save(categoryToUpdate);
  }

  public boolean isParentCategory(long id) {
    return categoryRepository.numberOfEntriesUsingThisCategoryAsParentId(id) != 0;
  }

  public boolean idExist(long id) {
    return categoryRepository.existsById(id);
  }

  public boolean canBeParentCategory(long categoryId, long parentCategoryId, long userId) {
    if (categoryId == parentCategoryId) {
      return false; // got cycle - one of the parents is trying to use it's child as parent
    }

    Optional<Category> parentCategoryOptional = getCategoryByIdAndUserId(parentCategoryId, userId);

    if (!parentCategoryOptional.isPresent()) {
      throw new IllegalStateException(String.format("Received parent category id (%d) which does not exists in database", parentCategoryId));
    }

    Category parentCategory = parentCategoryOptional.get();

    if (parentCategory.getParentCategory() == null) {
      return true; // we cannot continue as parent category is null but we need it's id. No parent is trying to use this category as parent so it's ok
    }

    // TODO - PERFORMANCE - maybe it's faster to retrieve first all and then do calculations, measure and compare
    return canBeParentCategory(categoryId, parentCategory.getParentCategory().getId(), userId);
  }

  public boolean isCategoryNameAlreadyUsed(String name, long userId) {
    return categoryRepository.findByNameIgnoreCaseAndUserId(name, userId).size() != 0;
  }

  public boolean categoryExistByIdAndUserId(long categoryId, long userId) {
    return categoryRepository.existsByIdAndUserId(categoryId, userId);
  }

}