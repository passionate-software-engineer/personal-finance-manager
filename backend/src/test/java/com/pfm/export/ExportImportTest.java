package com.pfm.export;

import static com.pfm.export.ImportService.CATEGORY_NAMED_IMPORTED;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.pfm.category.Category;
import com.pfm.category.CategoryRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExportImportTest {

  private static final long MOCK_USER_ID = 99L;

  @Mock
  private CategoryRepository categoryRepository;

  @Spy
  @InjectMocks
  private ImportService importService;

  @Test
  void shouldThrowExceptionWhenMoreThanOneCategoryWithNameImportedFoundDuringImportingData() {
    // given
    final Category importedCategory1 = Category.builder()
        .name(CATEGORY_NAMED_IMPORTED)
        .parentCategory(null)
        .userId(MOCK_USER_ID)
        .build();

    final Category importedCategory2 = Category.builder()
        .id(2L)
        .name(CATEGORY_NAMED_IMPORTED)
        .parentCategory(null)
        .userId(MOCK_USER_ID)
        .build();

    when(categoryRepository.findByNameIgnoreCaseAndUserId(CATEGORY_NAMED_IMPORTED, MOCK_USER_ID))
        .thenReturn(List.of(importedCategory1, importedCategory2));

    // then
    assertThrows(IllegalStateException.class, () -> importService.deleteCategoryNamedImportedFromDbIfAlreadyExistToPreventDuplication(MOCK_USER_ID));
  }
}
