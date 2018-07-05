package com.pfm.category;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class CategoryService {

  @Autowired // TODO - it's better to do dependency injection through constructor - please change to it
  private CategoryRepository categoryRepository;

  // TODO - it's not correct use of optional - you should return it further and then in controller check if value is present
  public Category getCategoryById(long id) {
    return categoryRepository.findById(id).get();
  }

  public List<Category> getCategories() {
    List<Category> categories = new ArrayList<>(); // TODO better to use: StreamSupport.stream(iterable.spliterator(), false)
    categoryRepository.findAll().forEach(category -> categories.add(category));
    categories.sort(Comparator.comparing(Category::getId)); // TODO when you will refactor to method above just use sorted() on stream and then collect
    return categories;
  }

  public Category addCategory(Category category) {
    return categoryRepository.save(category);
  }

  public void removeCategory(long id) {
    categoryRepository.deleteById(id);
  }

  // TODO - no need to extract value get whole Category object and take values from it.
  public Category updateCategory(long id, String name, Category parentCategory) {
    Category categoryToUpdate = getCategoryById(id);
    categoryToUpdate.setName(name);
    categoryToUpdate.setParentCategory(parentCategory);
    categoryRepository.save(categoryToUpdate);
    return categoryToUpdate;
  }
}
