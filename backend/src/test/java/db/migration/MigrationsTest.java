package db.migration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import com.pfm.Application;
import com.pfm.category.CategoryRepository;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
public class MigrationsTest {

  @Autowired
  protected Flyway flyway;

  @Autowired
  private CategoryRepository categoryRepository;

  @Test
  public void shouldExecuteAllMigrationsWithSuccess() {
    // TODO add check if correct currencies are added and accounts has correct default currency
    // TODO add check if correct account types are added and accounts has correct default account type

    // given
    flyway.clean();

    // when
    flyway.migrate();

    // then
    assertCategoriesWereConvertedToFlatStructure();
  }

  private void assertCategoriesWereConvertedToFlatStructure() {
    assertThat(categoryRepository.findById(1L).orElseThrow().getParentCategory(), nullValue());
    assertThat(categoryRepository.findById(2L).orElseThrow().getParentCategory().getId(), is(1L));
    assertThat(categoryRepository.findById(3L).orElseThrow().getParentCategory().getId(), is(1L));
    assertThat(categoryRepository.findById(4L).orElseThrow().getParentCategory().getId(), is(1L));
    assertThat(categoryRepository.findById(5L).orElseThrow().getParentCategory().getId(), is(1L));
    assertThat(categoryRepository.findById(6L).orElseThrow().getParentCategory(), nullValue());
    assertThat(categoryRepository.findById(7L).orElseThrow().getParentCategory().getId(), is(6L));
    assertThat(categoryRepository.findById(8L).orElseThrow().getParentCategory(), nullValue());
    assertThat(categoryRepository.findById(9L).orElseThrow().getParentCategory().getId(), is(8L));
    assertThat(categoryRepository.findById(10L).orElseThrow().getParentCategory().getId(), is(8L));
    assertThat(categoryRepository.findById(11L).orElseThrow().getParentCategory().getId(), is(8L));
    assertThat(categoryRepository.findById(12L).orElseThrow().getParentCategory().getId(), is(8L));
  }
}
