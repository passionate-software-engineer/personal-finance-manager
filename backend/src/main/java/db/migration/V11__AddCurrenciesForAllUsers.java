package db.migration;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.ResultSet;
import java.sql.Statement;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

// ENHANCEMENT remove after fixing https://github.com/spotbugs/spotbugs/issues/756
@SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
@SuppressWarnings("checkstyle:typename")
public class V11__AddCurrenciesForAllUsers extends BaseJavaMigration {

  // Support for currencies was added, need to assign default values for each account
  public void migrate(Context context) throws Exception {

    try (Statement select = context.getConnection().createStatement()) {
      try (ResultSet usersRows = select.executeQuery("SELECT id FROM app_user ORDER BY id")) {
        while (usersRows.next()) {
          int userId = usersRows.getInt(1);

          try (Statement insert = context.getConnection().createStatement()) {
            insert.execute("INSERT INTO currency (name, exchange_rate, user_id) VALUES "
                + "('PLN', 1.00, " + userId + "), "
                + "('USD', 3.58, " + userId + "), "
                + "('EUR', 4.24, " + userId + "), "
                + "('GBP', 4.99, " + userId + ")"
            );
          }

          try (Statement getPlnIdSelect = context.getConnection().createStatement()) {
            try (ResultSet getPlnIdSelectRows = getPlnIdSelect
                .executeQuery("SELECT id FROM currency WHERE user_id = " + userId + "AND name = 'PLN'")) {
              while (getPlnIdSelectRows.next()) {
                int defaultCurrencyId = getPlnIdSelectRows.getInt(1);

                try (Statement update = context.getConnection().createStatement()) {
                  update.execute("UPDATE account SET currency_id = " + defaultCurrencyId + " WHERE user_id = " + userId);
                }
              }
            }
          }
        }
      }
    }
  }
}