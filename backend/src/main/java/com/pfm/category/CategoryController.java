package com.pfm.category;

import static org.springframework.http.ResponseEntity.status;

import com.google.common.base.Preconditions;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("categories")
@RestController
@Slf4j
@AllArgsConstructor
@CrossOrigin
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryValidator categoryValidator;

    @GetMapping(value = "/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable long id) {
        Category category = categoryService.getCategoryById(id);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Category>> getCategories() {
        List<Category> categories = categoryService.getCategories();
        if (categories.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity addCategory(@RequestBody Category category) {
        List<String> validationResult = categoryValidator.validate(category);
        if (!validationResult.isEmpty()) {
            return ResponseEntity.badRequest().body(validationResult);
        }
        Category createdCategory = categoryService.addCategory(category);
        return new ResponseEntity<>(createdCategory.getId(), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        List<String> validationResult = categoryValidator.validate(category);
        if (!validationResult.isEmpty()) {
            return ResponseEntity.badRequest().body(validationResult);
        }
        if (id == null) {
            return ResponseEntity.notFound().build();
        }
        Category updatedCategory = categoryService.updateCategory(id, category.getName(),
            category.getParentCategory());
        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        if (categoryService.getCategoryById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        categoryService.removeCategory(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
