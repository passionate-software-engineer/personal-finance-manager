package com.pfm.category;

import com.pfm.account.Account;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    category.setParentCategory(getCategoryById(category.getParentCategory().getId())
        .orElse(null));
    return categoryRepository.save(category);
  }

  public void removeCategory(long id) {
    categoryRepository.deleteById(id);
  }

  public Category updateCategory(Category category) {
    Category categoryToUpdate = getCategoryById(category.getId()).get();
    categoryToUpdate.setName(category.getName());
    if (category.getParentCategory() == null) {
      categoryToUpdate.setParentCategory(null);
    } else {
      categoryToUpdate.setParentCategory(getCategoryById(category.getParentCategory().getId())
          .orElse(null));
    }
    categoryRepository.save(categoryToUpdate);
    return categoryToUpdate;
  }

  public boolean isParentCategory(long id) {
    return StreamSupport.stream(categoryRepository.findAll().spliterator(), false)
        .filter(category -> category.getParentCategory() != null)
        .anyMatch((category -> category.getParentCategory().getId() == id));
  }
}
