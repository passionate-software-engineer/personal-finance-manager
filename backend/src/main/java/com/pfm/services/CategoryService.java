package com.pfm.services;


import com.pfm.model.Category;
import com.pfm.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class CategoryService {

    @Autowired
    CategoryRepository categoryRepository;

    public Category getCategoryById(long id) {
        return categoryRepository.getOne(id);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
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
