package com.pfm.category;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public Category getCategoryById(long id) {
        return categoryRepository.getOne(id);
    }

    public List<Category> getCategories() {
      List<Category> categories = categoryRepository.findAll();
      categories.sort(Comparator.comparing(Category::getId));
        return categories;
    }

    public Category addCategory(Category category) {
        return categoryRepository.save(category);
    }

    public void removeCategory(long id) {
        categoryRepository.deleteById(id);
    }

    public Category updateCategory(long id, String name, Category parentCategory) {
        Category categoryToUpdate = getCategoryById(id);
        categoryToUpdate.setName(name);
        categoryToUpdate.setParentCategory(parentCategory);
        categoryRepository.save(categoryToUpdate);
        return categoryToUpdate;
    }
}
