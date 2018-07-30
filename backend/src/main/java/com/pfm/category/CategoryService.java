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
    // TODO - if parent category returned by DB is null then throw IllegalStateException
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

  public boolean canBeParentCategory(long categoryId, long parentCategoryId) {
    if (categoryId == parentCategoryId) {
      return false;
    }

    Category parentCategory = getCategoryById(parentCategoryId).orElse(null);
    // TODO - this check does not make sense - if parent category don't exists we should throw illegalStateException
    if (parentCategory == null) {
      return true;
    }
    // TODO - why? if parent category don't have parent then it's some special case? should be handled in query below
    if (parentCategory.getParentCategory() == null) {
      return true;
    }

    // TODO - I don't like this recursion - simply category can be parent category for other if it's not any of it's
    // children - we need to handle that with single query to db, can do some processing in java but not multiple calls to DB - hint DFS algorithm ;)
    return canBeParentCategory(categoryId, parentCategory.getParentCategory().getId());
  }

  public boolean isCategoryNameAlreadyUsed(String name) {
    return categoryRepository.findByNameIgnoreCase(name).size() != 0;
  }

}