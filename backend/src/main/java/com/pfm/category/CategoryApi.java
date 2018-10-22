package com.pfm.category;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("categories")
@CrossOrigin
@Api(value = "Category", description = "Controller used to list / add / update / delete categories.")
public interface CategoryApi {

  @ApiOperation(value = "Find category by id", response = Category.class)
  @GetMapping(value = "/{categoryId}")
  ResponseEntity<Category> getCategoryById(long categoryId, long userId);

  @ApiOperation(value = "Get list of categories", response = Category.class, responseContainer = "List")
  @GetMapping
  ResponseEntity<List<Category>> getCategories(long userId);

  @ApiOperation(value = "Create a new category", response = Long.class)
  @PostMapping
  ResponseEntity<?> addCategory(CategoryRequest categoryRequest, long userId);

  @ApiOperation(value = "Update an existing category", response = Void.class)
  @PutMapping(value = "/{categoryId}")
  ResponseEntity<?> updateCategory(long categoryId, CategoryRequest categoryRequest, long userId);

  @ApiOperation(value = "Delete an existing category", response = Void.class)
  @DeleteMapping(value = "/{categoryId}")
  ResponseEntity<?> deleteCategory(long categoryId, long userId);

}
