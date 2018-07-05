package com.pfm.category;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("categories")
@RestController
@Slf4j
@AllArgsConstructor
@CrossOrigin
public class CategoryController {

  @Autowired // TODO - it's better to do dependency injection through constructor - please change to it
  private CategoryService categoryService;

  @Autowired // TODO - it's better to do dependency injection through constructor - please change to it
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
    if (categories.isEmpty()) { // TODO - it's not correct - you should return simply empty list
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

    if (id == null) { // TODO - it's not correct - if null is provided then validation error should be returned, when correct but not existing value is provided then notfound
      return ResponseEntity.notFound().build();
    }

    // TODO - why do you extract fields? pass entire object to method.
    Category updatedCategory = categoryService.updateCategory(id, category.getName(),
        category.getParentCategory());

    return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
  }

  @DeleteMapping(value = "/{id}")
  public ResponseEntity<Void> deleteCategory(@PathVariable Long id) { // TODO - what if id is null? maybe you should change to long and allow Spring to throw error?
    if (categoryService.getCategoryById(id) == null) { // TODO - use optional instead - always when you check for null prefer optional
      return ResponseEntity.notFound().build();
    }
    categoryService.removeCategory(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
