package db.migration;

import com.pfm.Application;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
public class CurrencyMigrationTest {

  @Autowired
  protected Flyway flyway;

  @Test
  public void shouldAddCurrencyToAllAccounts() {
    flyway.clean();
    flyway.migrate();

    // No need to check anything - migration will fail on setting NOT NULL if any account was missed.
    // TODO add check if correct currencies are added and accounts has correct default currency
  }
}
