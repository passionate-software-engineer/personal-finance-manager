package com.pfm.category;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pfm.Messages;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Optional;

@RequestMapping("categories")
@RestController
@Slf4j
@AllArgsConstructor
@CrossOrigin
public class CategoryController {

  private CategoryService categoryService;
  private CategoryValidator categoryValidator;

  @ApiOperation(value = "Get category with an ID", notes = "Get a category with specific ID")
  @GetMapping(value = "/{id}")
  public ResponseEntity getCategoryById(@PathVariable long id) {
    Optional<Category> category = categoryService.getCategoryById(id);
    if (category.isPresent()) {
      return ResponseEntity.ok(category.get());
    }
    return ResponseEntity.notFound().build();
  }

  @ApiOperation(value = "Get list of categories", notes = "Get all categories in database")
  @GetMapping
  public ResponseEntity<List<Category>> getCategories() {
    List<Category> categories = categoryService.getCategories();
    return ResponseEntity.ok(categories);
  }

  @ApiOperation(value = "Create a new category", notes = "Creating a new category")
  @PostMapping
  public ResponseEntity addCategory(@RequestBody CategoryWithoutId categoryWithoutId) {
    // must copy as types do not match for Hibernate
    Category category = new Category(null, categoryWithoutId.getName(),
        categoryWithoutId.getParentCategory());

    List<String> validationResult = categoryValidator.validate(category);
    if (!validationResult.isEmpty()) {
      return ResponseEntity.badRequest().body(validationResult);
    }
    Category createdCategory = categoryService.addCategory(category);
    return ResponseEntity.ok(createdCategory.getId());
  }

  @ApiOperation(value = "Update a category with ID", notes = "Update a category with specific ID")
  @PutMapping(value = "/{id}")
  public ResponseEntity updateCategory(@PathVariable long id,
      @RequestBody CategoryWithoutId categoryWithoutId) {
    if (!categoryService.idExist(id)) {
      return ResponseEntity.notFound().build();
    }
    // must copy as types do not match for Hibernate
    Category category = new Category(id, categoryWithoutId.getName(),
        categoryWithoutId.getParentCategory());

    List<String> validationResult = categoryValidator.updateValidate(category);
    if (!validationResult.isEmpty()) {
      return ResponseEntity.badRequest().body(validationResult);
    }
    categoryService.updateCategory(id, category);
    return ResponseEntity.ok().build();
  }

  @ApiOperation(value = "Delete a category with ID", notes = "Deleting a category with specific ID")
  @DeleteMapping(value = "/{id}")
  public ResponseEntity deleteCategory(@PathVariable long id) {
    if (!categoryService.getCategoryById(id).isPresent()) {
      return ResponseEntity.notFound().build();
    }
    if (categoryService.isParentCategory(id)) {
      return ResponseEntity.badRequest().body(Messages.CANNOT_DELETE_PARENT_CATEGORY);
    }
    categoryService.deleteCategory(id);
    return ResponseEntity.ok().build();
  }

  private static class CategoryWithoutId extends Category {

    @JsonIgnore
    public void setId(Long id) {
      super.setId(id);
    }
  }
}
