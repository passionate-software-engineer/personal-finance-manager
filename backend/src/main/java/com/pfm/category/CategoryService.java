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

  public void removeCategory(long id) {
    categoryRepository.deleteById(id); // TODO remove or delete?
  }

  public void updateCategory(Category category) { // TODO pass id and set it inside
    Category categoryToUpdate = getCategoryById(category.getId()).get(); // TODO IllegalStateException
    categoryToUpdate.setName(category.getName());
    if (category.getParentCategory() == null) {
      categoryToUpdate.setParentCategory(null);
    } else {
      Optional<Category> parentCategory = getCategoryById(category.getParentCategory().getId());
      categoryToUpdate.setParentCategory(parentCategory.orElse(null)); // TODO unify :)
    }
    categoryRepository.save(categoryToUpdate);
  }

  public boolean isParentCategory(long id) {
    return StreamSupport.stream(categoryRepository.findAll().spliterator(), false) // TODO not optimal please write query
        .filter(category -> category.getParentCategory() != null)
        .anyMatch((category -> category.getParentCategory().getId() == id));
  }

  public boolean idExist(long id) {
    return categoryRepository.existsById(id);
  }
}
