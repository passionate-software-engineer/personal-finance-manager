package com.pfm.category;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@AllArgsConstructor
public class CategoryService {

  private CategoryRepository categoryRepository;

  public Optional<Category> getCategoryById(long id) {
    return categoryRepository.findById(id);
  }

  public List<Category> getCategories() {
    return StreamSupport.stream(categoryRepository.findAll().spliterator(), false)
        .sorted(Comparator.comparing(Category::getId))
        .collect(Collectors.toList());
  }

  public Category addCategory(Category category) {
    if (category.getParentCategory() == null) {
      return categoryRepository.save(category);
    }
    Category parentCategory = getCategoryById(category.getParentCategory().getId()).orElse(null);
    category.setParentCategory(parentCategory);
    return categoryRepository.save(category);
  }

  public void deleteCategory(long id) {
    categoryRepository.deleteById(id);
  }

  public void updateCategory(long id, Category category) {
    Optional<Category> receivedCategory = getCategoryById(id);
    if (!receivedCategory.isPresent()) {
      throw new IllegalStateException("Category with id : " + id + " does not exist in database");
    }
    Category categoryToUpdate = receivedCategory.get();
    categoryToUpdate.setName(category.getName());
    if (category.getParentCategory() == null) {
      categoryToUpdate.setParentCategory(null);
    } else {
      Optional<Category> parentCategory = getCategoryById(category.getParentCategory().getId());
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

  public boolean canBeParentCategory(long rootCategoryId, long possibleParentCategoryId) {
    if (rootCategoryId == possibleParentCategoryId) {
      return false;
    }
    Category receivedCategory = getCategoryById(possibleParentCategoryId).orElse(null);
    if (receivedCategory == null) {
      return true;
    }
    if (receivedCategory.getId() == rootCategoryId) {
      return false;
    }
    if (receivedCategory.getParentCategory() == null) {
      return true;
    }
    return canBeParentCategory(rootCategoryId, receivedCategory.getParentCategory().getId());
  }

  public boolean nameExist(String name) {
    return categoryRepository.findByNameContainingIgnoreCase(name).size() != 0;
  }


}
