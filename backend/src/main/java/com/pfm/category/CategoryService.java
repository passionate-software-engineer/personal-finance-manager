package com.pfm.category;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class CategoryService {

  private CategoryRepository categoryRepository;

  public Optional<Category> getCategoryById(long id, long userId) {
    return categoryRepository.findByIdAndUserId(id, userId);
  }

  //leave this method for transactionApi to work now
  public Optional<Category> getCategoryById(long id) {
    return categoryRepository.findById(id);
  }

  public List<Category> getCategories(long userId) {
    return StreamSupport.stream(categoryRepository.findByUserId(userId).spliterator(), false)
        .sorted(Comparator.comparing(Category::getId))
        .collect(Collectors.toList());
  }

  public Category addCategory(Category category, long userId) {
    if (category.getParentCategory() == null) {
      return categoryRepository.save(category);
    }
    // TODO - if parent category returned by DB is null then throw IllegalStateException
    Category parentCategory = getCategoryById(category.getParentCategory().getId(), userId).orElse(null);
    category.setParentCategory(parentCategory);
    return categoryRepository.save(category);
  }

  public void deleteCategory(long id) {
    categoryRepository.deleteById(id);
  }

  public void updateCategory(long id, Category category, long userId) {
    Optional<Category> receivedCategory = getCategoryById(id, userId);
    if (!receivedCategory.isPresent()) {
      throw new IllegalStateException("Category with id : " + id + " does not exist in database");
    }

    Category categoryToUpdate = receivedCategory.get();
    categoryToUpdate.setName(category.getName());
    if (category.getParentCategory() == null) {
      categoryToUpdate.setParentCategory(null);
    } else {
      Optional<Category> parentCategory = getCategoryById(category.getParentCategory().getId(), userId);
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

  public boolean canBeParentCategory(long categoryId, long parentCategoryId) {
    if (categoryId == parentCategoryId) {
      return false; // got cycle - one of the parents is trying to use it's child as parent
    }

    Optional<Category> parentCategoryOptional = getCategoryById(parentCategoryId);

    if (!parentCategoryOptional.isPresent()) {
      throw new IllegalStateException(String.format("Received parent category id (%d) which does not exists in database", parentCategoryId));
    }

    Category parentCategory = parentCategoryOptional.get();

    if (parentCategory.getParentCategory() == null) {
      return true; // we cannot continue as parent category is null but we need it's id. No parent is trying to use this category as parent so it's ok
    }

    // TODO - PERFORMANCE - maybe it's faster to retrieve first all and then do calculations, measure and compare
    return canBeParentCategory(categoryId, parentCategory.getParentCategory().getId());
  }

  public boolean isCategoryNameAlreadyUsed(String name) {
    return categoryRepository.findByNameIgnoreCase(name).size() != 0;
  }

}