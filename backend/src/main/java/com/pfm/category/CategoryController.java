package com.pfm.category;

import com.pfm.Messages;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Optional;

@RequestMapping("categories")
@RestController
@Slf4j
@AllArgsConstructor
@CrossOrigin
public class CategoryController {

  private CategoryService categoryService;
  private CategoryValidator categoryValidator;

  @GetMapping(value = "/{id}")
  public ResponseEntity getCategoryById(@PathVariable long id) {
    Optional<Category> category = categoryService.getCategoryById(id);
    if (category.isPresent()) {
      return new ResponseEntity<>(category.get(), HttpStatus.OK);
    }
    return ResponseEntity.notFound().build();
  }

  @GetMapping
  public ResponseEntity<List<Category>> getCategories() {
    List<Category> categories = categoryService.getCategories();
    return new ResponseEntity<>(categories, HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity addCategory(@RequestBody Category category) {
    if (category.getId() != null && categoryService.idExist(category.getId())) { // TODO should be handled by validator
      return ResponseEntity.badRequest().body(Messages.ADD_CATEGORY_PROVIDED_ID_ALREADY_EXIST);
    }
    List<String> validationResult = categoryValidator.validate(category);
    if (!validationResult.isEmpty()) {
      return ResponseEntity.badRequest().body(validationResult);
    }
    Category createdCategory = categoryService.addCategory(category);
    return new ResponseEntity<>(createdCategory.getId(), HttpStatus.OK);
  }

  @PutMapping(value = "/{id}")
  public ResponseEntity updateCategory(@PathVariable long id, @RequestBody Category category) {
    if (!categoryService.idExist(id)) {
      return ResponseEntity.badRequest().body(Messages.UPDATE_CATEGORY_NO_ID_OR_ID_NOT_EXIST); // TODO return not found
    }
    category.setId(id); // TODO cover with tests
    List<String> validationResult = categoryValidator.validate(category);
    if (!validationResult.isEmpty()) {
      return ResponseEntity.badRequest().body(validationResult);
    }
    categoryService.updateCategory(category);
    return ResponseEntity.ok().build(); // TODO unify
  }

  @DeleteMapping(value = "/{id}")
  public ResponseEntity deleteCategory(@PathVariable long id) {
    if (!categoryService.getCategoryById(id).isPresent()) {
      return ResponseEntity.notFound().build(); // TODO unify approach ! !! :)
    }
    if (categoryService.isParentCategory(id)) {
      return ResponseEntity.badRequest().body(Messages.CANNOT_DELETE_PARENT_CATEGORY);
    }
    categoryService.removeCategory(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
