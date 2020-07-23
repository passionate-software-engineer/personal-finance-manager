package com.pfm.export.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import com.pfm.category.Category;
import com.pfm.category.CategoryService;
import com.pfm.export.ExportResult.ExportCategory;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryExporterTest {

  private static final long USER_ID = 1L;

  @Mock
  private CategoryService categoryService;

  @InjectMocks
  private CategoryExporter categoryExporter;

  @Test
  public void shouldConvertCategoryToCategoryExport() {
    // given
    Category category = createCategory();
    when(categoryService.getCategories(USER_ID)).thenReturn(List.of(category));

    // when
    List<ExportCategory> export = categoryExporter.export(USER_ID);

    // then
    assertEquals(export.size(), 1);
    ExportCategory exportCategory = export.get(0);
    assertEquals(exportCategory.getName(), category.getName());
    assertEquals(exportCategory.getPriority(), category.getPriority());
    assertNull(exportCategory.getParentCategoryName());
  }

  private Category createCategory() {
    return Category
        .builder()
        .name("CATEGORY_NAME")
        .priority(15)
        .build();
  }
}
