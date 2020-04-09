package db.migration;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

// TODO remove after fixing https://github.com/spotbugs/spotbugs/issues/756
@SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
@SuppressWarnings("checkstyle:typename")
public class V25__AddCategoryPriorityAndFlatCategoriesStructureForAllUsers extends BaseJavaMigration {

  // Support for accountTypes was added, need to assign default values for each account
  @Override
  public void migrate(Context context) throws Exception {
    Map<Long, Long> categoryToParentCategoryMap = new HashMap<>();
    try (Statement select = context.getConnection().createStatement()) {
      try (ResultSet categoryRows = select.executeQuery("SELECT * FROM category")) {
        while (categoryRows.next()) {
          categoryToParentCategoryMap.put(categoryRows.getLong(1), categoryRows.getLong(3));
        }
      }
    }

    for (Map.Entry<Long, Long> entry : categoryToParentCategoryMap.entrySet()) {
      if (entry.getValue() == 0 || categoryToParentCategoryMap.get(entry.getValue()) == 0) {
        continue;
      }

      long parentCategoryId = categoryToParentCategoryMap.get(entry.getValue());
      do {
        parentCategoryId = categoryToParentCategoryMap.get(parentCategoryId);
      } while (categoryToParentCategoryMap.get(parentCategoryId) != 0);

      try (Statement update = context.getConnection().createStatement()) {
        update.execute(String.format("UPDATE category SET parent_category_id = %d WHERE id = %d", parentCategoryId, entry.getKey()));
      }
    }
  }
}

