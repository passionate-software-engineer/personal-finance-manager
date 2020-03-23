package db.migration;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.ResultSet;
import java.sql.Statement;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

// TODO remove after fixing https://github.com/spotbugs/spotbugs/issues/756
@SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
@SuppressWarnings("checkstyle:typename")
public class V22__AddAccountTypesForAllUsers extends BaseJavaMigration {

  // Support for accountTypes was added, need to assign default values for each account
  @Override
  public void migrate(Context context) throws Exception {

    try (Statement select = context.getConnection().createStatement()) {
      try (ResultSet usersRows = select.executeQuery("SELECT id FROM app_user ORDER BY id")) {
        while (usersRows.next()) {
          int userId = usersRows.getInt(1);

          try (Statement insert = context.getConnection().createStatement()) {
            insert.execute("INSERT INTO account_type (name, user_id) VALUES "
                + "('Personal', " + userId + "), "
                + "('Investment', " + userId + "), "
                + "('Saving', " + userId + "), "
                + "('Credit', " + userId + ")"
            );
          }

          try (Statement getPersonalIdSelect = context.getConnection().createStatement()) {
            try (ResultSet getPersonalIdSelectRows = getPersonalIdSelect
                .executeQuery("SELECT id FROM account_type WHERE user_id = " + userId + "AND name = 'Personal'")) {
              while (getPersonalIdSelectRows.next()) {
                int defaultAccountTypeId = getPersonalIdSelectRows.getInt(1);

                try (Statement update = context.getConnection().createStatement()) {
                  update.execute("UPDATE account SET type_id = " + defaultAccountTypeId + " WHERE user_id = " + userId);
                }
              }
            }
          }
        }
      }
    }
  }
}
