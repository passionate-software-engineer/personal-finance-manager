package com.pfm.category;

import com.pfm.category.requests.CategoryAddRequest;
import com.pfm.category.requests.CategoryUpdateRequest;
import com.pfm.swagger.ApiConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("categories")
@CrossOrigin
@Api(tags = {"category-controller"})
public interface CategoryApi {

  String BEARER = "Bearer";

  @ApiOperation(value = "Find category by id", authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = ApiConstants._200_OK_MESSAGE, response = Category.class),
      @ApiResponse(code = 401, message = ApiConstants._401_UN_AUTH_MESSAGE, response = String.class),
      @ApiResponse(code = 404, message = ApiConstants._404_NOT_FOUND_MESSAGE),
  })
  @GetMapping(value = "/{categoryId}")
  ResponseEntity<Category> getCategoryById(@PathVariable long categoryId);

  @ApiOperation(value = "Get list of categories", authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = ApiConstants._200_OK_MESSAGE, response = Category.class, responseContainer = "list"),
      @ApiResponse(code = 401, message = ApiConstants._401_UN_AUTH_MESSAGE, response = String.class),
  })
  @GetMapping
  ResponseEntity<List<Category>> getCategories();

  @ApiOperation(value = "Create a new category", authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = ApiConstants._200_OK_MESSAGE, response = Long.class),
      @ApiResponse(code = 400, message = ApiConstants._400_BAD_REQ_MESSAGE, response = String.class, responseContainer = "list"),
      @ApiResponse(code = 401, message = ApiConstants._401_UN_AUTH_MESSAGE, response = String.class),
  })
  @PostMapping
  ResponseEntity<?> addCategory(CategoryAddRequest categoryRequest);

  @ApiOperation(value = "Update an existing category", authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = ApiConstants._200_OK_MESSAGE),
      @ApiResponse(code = 400, message = ApiConstants._400_BAD_REQ_MESSAGE, response = String.class, responseContainer = "list"),
      @ApiResponse(code = 401, message = ApiConstants._401_UN_AUTH_MESSAGE, response = String.class),
  })
  @PutMapping(value = "/{categoryId}")
  ResponseEntity<?> updateCategory(@PathVariable long categoryId, CategoryUpdateRequest categoryRequest);

  @ApiOperation(value = "Delete an existing category", authorizations = {@Authorization(value = BEARER)})
  @ApiResponses({
      @ApiResponse(code = 200, message = ApiConstants._200_OK_MESSAGE),
      @ApiResponse(code = 400, message = ApiConstants._400_BAD_REQ_MESSAGE, response = String.class),
      @ApiResponse(code = 401, message = ApiConstants._401_UN_AUTH_MESSAGE, response = String.class),
      @ApiResponse(code = 404, message = ApiConstants._404_NOT_FOUND_MESSAGE),
  })
  @DeleteMapping(value = "/{categoryId}")
  ResponseEntity<?> deleteCategory(@PathVariable long categoryId);

}
