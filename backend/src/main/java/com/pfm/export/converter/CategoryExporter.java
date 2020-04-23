package com.pfm.export.converter;

import com.pfm.category.Category;
import com.pfm.category.CategoryService;
import com.pfm.export.ExportResult.ExportCategory;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CategoryExporter {

  private final CategoryService categoryService;

  public List<ExportCategory> export(long userId) {
    return categoryService.getCategories(userId)
        .stream()
        .map(this::mapToExportCategory)
        .collect(Collectors.toList());
  }

  private ExportCategory mapToExportCategory(Category category) {
    return ExportCategory.builder()
        .name(category.getName())
        .priority(category.getPriority())
        .parentCategoryName(category.getParentCategory() != null ? category.getParentCategory().getName() : null)
        .build();
  }

}
