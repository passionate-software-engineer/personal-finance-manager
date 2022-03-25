package com.pfm.category;

import static com.pfm.config.SwaggerConfig.SECURITY_SCHEME_NAME;
import static com.pfm.helpers.http.HttpCodesAsString.BAD_REQUEST;
import static com.pfm.helpers.http.HttpCodesAsString.NOT_FOUND;
import static com.pfm.helpers.http.HttpCodesAsString.OK;
import static com.pfm.helpers.http.HttpCodesAsString.UNAUTHORIZED;
import static com.pfm.swagger.ApiConstants.BAD_REQUEST_MESSAGE;
import static com.pfm.swagger.ApiConstants.NOT_FOUND_MESSAGE;
import static com.pfm.swagger.ApiConstants.OK_MESSAGE;
import static com.pfm.swagger.ApiConstants.UNAUTHORIZED_MESSAGE;

import com.pfm.category.requests.CategoryAddRequest;
import com.pfm.category.requests.CategoryUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@SecurityRequirement(name = SECURITY_SCHEME_NAME)
@Tag(name = "Category Controller", description = "Controller used to list / add / update / delete categories.")
public interface CategoryApi {

  @Operation(summary = "Find category by id")
  @ApiResponses({
      @ApiResponse(responseCode = OK, description = OK_MESSAGE, content = {@Content(
          mediaType = "application/json", schema = @Schema(implementation = Category.class))}),
      @ApiResponse(responseCode = UNAUTHORIZED, description = UNAUTHORIZED_MESSAGE, content = {@Content(
          schema = @Schema(implementation = String.class))}),
      @ApiResponse(responseCode = NOT_FOUND, description = NOT_FOUND_MESSAGE)
  })
  @GetMapping(value = "/{categoryId}")
  ResponseEntity<Category> getCategoryById(@PathVariable long categoryId);

  @Operation(summary = "Get list of categories")
  @ApiResponses({
      @ApiResponse(responseCode = OK, description = OK_MESSAGE, content = {@Content(
          array = @ArraySchema(schema = @Schema(implementation = Category.class)))}),
      @ApiResponse(responseCode = UNAUTHORIZED, description = UNAUTHORIZED_MESSAGE, content = {@Content(
          schema = @Schema(implementation = String.class))}),
  })
  @GetMapping
  ResponseEntity<List<Category>> getCategories();

  @Operation(summary = "Create a new category")
  @ApiResponses({
      @ApiResponse(responseCode = OK, description = OK_MESSAGE, content = {@Content(
          schema = @Schema(implementation = Long.class))}),
      @ApiResponse(responseCode = BAD_REQUEST, description = BAD_REQUEST_MESSAGE, content = {@Content(
          array = @ArraySchema(schema = @Schema(implementation = String.class)))}),
      @ApiResponse(responseCode = UNAUTHORIZED, description = UNAUTHORIZED_MESSAGE, content = {@Content(
          schema = @Schema(implementation = String.class))})
  })
  @PostMapping
  ResponseEntity<?> addCategory(CategoryAddRequest categoryRequest);

  @Operation(summary = "Update an existing category")
  @ApiResponses({
      @ApiResponse(responseCode = OK, description = OK_MESSAGE),
      @ApiResponse(responseCode = BAD_REQUEST, description = BAD_REQUEST_MESSAGE, content = {@Content(
          array = @ArraySchema(schema = @Schema(implementation = String.class)))}),
      @ApiResponse(responseCode = UNAUTHORIZED, description = UNAUTHORIZED_MESSAGE, content = {@Content(
          schema = @Schema(implementation = String.class))})
  })
  @PutMapping(value = "/{categoryId}")
  ResponseEntity<?> updateCategory(@PathVariable long categoryId, CategoryUpdateRequest categoryRequest);

  @Operation(summary = "Delete an existing category")
  @ApiResponses({
      @ApiResponse(responseCode = OK, description = OK_MESSAGE),
      @ApiResponse(responseCode = UNAUTHORIZED, description = UNAUTHORIZED_MESSAGE, content = {@Content(
          schema = @Schema(implementation = String.class))}),
      @ApiResponse(responseCode = NOT_FOUND, description = NOT_FOUND_MESSAGE)
  })
  @DeleteMapping(value = "/{categoryId}")
  ResponseEntity<?> deleteCategory(@PathVariable long categoryId);
}
