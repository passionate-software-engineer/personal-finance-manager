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

    @GetMapping(value = "/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable long id) {
        Category category = categoryService.getCategoryById(id);
        Preconditions.checkNotNull(category,
            "Could not get category with id %s", id);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @GetMapping
    public List<Category> getCategories() {
        List<Category> categories = categoryService.getCategories();
        Preconditions.checkNotNull(categories,
            "Could not get categories");
        return categories;
    }

    @PostMapping
    public ResponseEntity addCategory(@RequestBody Category category) {
        Preconditions.checkNotNull(category,
            "Invalid category");
        long id = categoryService.addCategory(category).getId();
        Preconditions.checkNotNull(id,
            "Could not add category");
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        Preconditions.checkNotNull(id,
            "Could not find category with id %s", id);
        Category updatedCategory = categoryService.updateCategory(id, category.getName(),
            category.getParentCategory());
        Preconditions.checkNotNull(updatedCategory,
            "Could not update category with id %s", id);
        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        Preconditions.checkNotNull(id,
            "Could not find category with id %s", id);
        categoryService.removeCategory(id);
        Preconditions.checkArgument(getCategoryById(id) == null,
            "Could not delete category with id %s", id );
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
