package com.pfm.category;

import static com.pfm.config.MessagesProvider.CANNOT_DELETE_PARENT_CATEGORY;
import static com.pfm.config.MessagesProvider.getMessage;

import com.pfm.history.HistoryEntryService;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
public class CategoryController implements CategoryApi {

  private CategoryService categoryService;
  private CategoryValidator categoryValidator;
  private HistoryEntryService historyEntryService;

  public ResponseEntity<Category> getCategoryById(@PathVariable long categoryId, @RequestAttribute(value = "userId") long userId) {

    log.info("Retrieving category with id: {}", categoryId);
    Optional<Category> category = categoryService.getCategoryByIdAndUserId(categoryId, userId);

    if (!category.isPresent()) {
      log.info("Category with id {} was not found", categoryId);
      return ResponseEntity.notFound().build();
    }

    log.info("Category with id {} was successfully retrieved", categoryId);
    return ResponseEntity.ok(category.get());
  }

  // TODO return only parent category id - not the entire object / objects chain
  public ResponseEntity<List<Category>> getCategories(@RequestAttribute(value = "userId") long userId) {

    log.info("Retrieving categories from database");
    List<Category> categories = categoryService.getCategories(userId);

    return ResponseEntity.ok(categories);
  }

  public ResponseEntity<?> addCategory(@RequestBody CategoryRequest categoryRequest, @RequestAttribute(value = "userId") long userId) {

    log.info("Saving category {} to the database", categoryRequest.getName());
    Category category = convertToCategory(categoryRequest, userId);

    List<String> validationResult = categoryValidator.validateCategoryForAdd(category, userId);
    if (!validationResult.isEmpty()) {
      log.info("Category is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }

    Category createdCategory = categoryService.addCategory(category, userId);
    log.info("Saving category to the database was successful. Category id is {}", createdCategory.getId());
    historyEntryService.addEntryOnAdd(category, userId);

    return ResponseEntity.ok(createdCategory.getId());
  }

  // TODO add transactions to all methods working with history service
  public ResponseEntity<?> updateCategory(@PathVariable long categoryId, @RequestBody CategoryRequest categoryRequest,
      @RequestAttribute(value = "userId") long userId) {

    if (!categoryService.getCategoryByIdAndUserId(categoryId, userId).isPresent()) {
      log.info("No category with id {} was found, not able to update", categoryId);
      return ResponseEntity.notFound().build();
    }

    Category category = convertToCategory(categoryRequest, userId);
    category.setId(categoryId);

    List<String> validationResult = categoryValidator.validateCategoryForUpdate(categoryId, userId, category);
    if (!validationResult.isEmpty()) {
      log.error("Category is not valid {}", validationResult);
      return ResponseEntity.badRequest().body(validationResult);
    }

    if (category.getParentCategory() != null) {
      category.setParentCategory(categoryService.getCategoryByIdAndUserId(category.getParentCategory().getId(), userId).get());
    }

    Category categoryToUpdate = categoryService.getCategoryByIdAndUserId(categoryId, userId).get();
    historyEntryService.addEntryOnUpdate(categoryToUpdate, category, userId);

    categoryService.updateCategory(categoryId, userId, category);
    log.info("Category with id {} was successfully updated", categoryId);
    return ResponseEntity.ok().build();
  }

  public ResponseEntity<?> deleteCategory(@PathVariable long categoryId, @RequestAttribute(value = "userId") long userId) {

    if (!categoryService.getCategoryByIdAndUserId(categoryId, userId).isPresent()) {
      log.info("No category with id {} was found, not able to delete", categoryId);
      return ResponseEntity.notFound().build();
    }

    if (categoryService.isParentCategory(categoryId)) {
      log.info("Category is used as parent. category Delete category {} not possible", categoryId);
      return ResponseEntity.badRequest().body(getMessage(CANNOT_DELETE_PARENT_CATEGORY));
    }

    List<String> validationResults = categoryValidator.validateCategoryForDelete(categoryId);
    if (!validationResults.isEmpty()) {
      log.info("Category with id {} was found, in transaction or filter, not able to delete", categoryId);
      return ResponseEntity.badRequest().body(validationResults);
    }

    log.info("Attempting to delete category with id {}", categoryId);

    // TODO Optional without isPresent - look for other occurences
    Category deletedCategory = categoryService.getCategoryByIdAndUserId(categoryId, userId).get();

    categoryService.deleteCategory(categoryId);

    log.info("Category with id {} was deleted successfully", categoryId);
    historyEntryService.addEntryOnDelete(deletedCategory, userId);
    return ResponseEntity.ok().build();
  }

  private static Category convertToCategory(@RequestBody CategoryRequest categoryRequest, long userId) {
    Long parentCategoryId = categoryRequest.getParentCategoryId();

    if (parentCategoryId == null) {
      return Category.builder()
          .id(null)
          .name(categoryRequest.getName())
          .parentCategory(null)
          .userId(userId) // TODO userId should be handled by services and passed to it explicitely
          .build();
    }

    return Category.builder()
        .id(null)
        .name(categoryRequest.getName())
        .parentCategory(Category.builder().id(parentCategoryId).build())
        .userId(userId)
        .build();
  }
}