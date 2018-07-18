package com.pfm.category;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pfm.Messages;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

@RequestMapping("categories")
@RestController
@Slf4j
@AllArgsConstructor
@CrossOrigin
@Api(value = "Category", description = "Controller used to list / add / update / delete categories.")
public class CategoryController {

  private CategoryService categoryService;
  private CategoryValidator categoryValidator;

  // TODO - add loging similar way it is added in Account Controller
  @ApiOperation(value = "Find category by id", response = Category.class)
  @GetMapping(value = "/{id}")
  public ResponseEntity<Category> getCategoryById(@PathVariable long id) {
    Optional<Category> category = categoryService.getCategoryById(id);
    if (!category.isPresent()) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(category.get());
  }

  @ApiOperation(value = "Get list of categories", response = Category.class, responseContainer = "List")
  @GetMapping
  public ResponseEntity<List<Category>> getCategories() {
    List<Category> categories = categoryService.getCategories();
    return ResponseEntity.ok(categories);
  }

  @ApiOperation(value = "Create a new category", response = Long.class)
  @PostMapping
  public ResponseEntity<?> addCategory(@RequestBody CategoryRequest categoryRequest) {
    Category category = convertToCategory(categoryRequest);

    List<String> validationResult = categoryValidator.validateCategoryForAdd(category);
    if (!validationResult.isEmpty()) {
      return ResponseEntity.badRequest().body(validationResult);
    }
    Category createdCategory = categoryService.addCategory(category);
    return ResponseEntity.ok(createdCategory.getId());
  }

  @ApiOperation(value = "Update an existing category", response = Void.class)
  @PutMapping(value = "/{id}")
  public ResponseEntity<?> updateCategory(@PathVariable long id,
      @RequestBody CategoryRequest categoryRequest) {
    if (!categoryService.idExist(id)) {
      return ResponseEntity.notFound().build();
    }

    Category category = convertToCategory(categoryRequest);
    category.setId(id);

    List<String> validationResult = categoryValidator.validateCategoryForUpdate(category);
    if (!validationResult.isEmpty()) {
      return ResponseEntity.badRequest().body(validationResult);
    }

    categoryService.updateCategory(id, category);
    return ResponseEntity.ok().build();
  }

  @ApiOperation(value = "Delete an existing category", response = Void.class)
  @DeleteMapping(value = "/{id}")
  public ResponseEntity<?> deleteCategory(@PathVariable long id) {
    if (!categoryService.getCategoryById(id).isPresent()) {
      return ResponseEntity.notFound().build();
    }
    if (categoryService.isParentCategory(id)) {
      return ResponseEntity.badRequest().body(Messages.CANNOT_DELETE_PARENT_CATEGORY);
    }
    categoryService.deleteCategory(id);
    return ResponseEntity.ok().build();
  }

  // hack to get different object for request and response
  @JsonIgnoreProperties(ignoreUnknown = true)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  @Data
  static class CategoryRequest {

    @Setter
    @Getter
    @ApiModelProperty(value = "Parent category id", example = "1")
    private Long parentCategoryId;

    @ApiModelProperty(value = "Category name", required = true, example = "Eating out")
    private String name;
  }

  static Category convertToCategory(@RequestBody CategoryRequest categoryRequest) {
    Long parentCategoryId = categoryRequest.getParentCategoryId();

    if (parentCategoryId == null) {
      return new Category(null, categoryRequest.getName(), null);
    }

    return new Category(null, categoryRequest.getName(),
        new Category(parentCategoryId, null, null));
  }
}