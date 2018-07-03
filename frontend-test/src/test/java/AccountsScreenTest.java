import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class AccountsScreenTest extends TestBase {

  private static final String FRONTEND_URL = "http://localhost:4200/";
  private AccountsScreenElements accountsElements;

  @BeforeClass
  void getElements() {
    accountsElements = new AccountsScreenElements(webDriver);
  }

  public AccountsScreenTest() {
    super(FRONTEND_URL);
  }

  @Test
  public void shouldReadPageTitle() {
    //given

    String titleExpected = "Welcome to Personal Finance Manager !";
    //when

    String titleResult = accountsElements.getTitle();
    //then

    assertThat(titleResult, is(equalTo(titleExpected)));
  }

  @Test
  public void shouldReadId() {
    //given

    long expectedId = 1L;
    //when

    long resultId = accountsElements.getId();
    //then

    assertThat(resultId, is(equalTo(expectedId)));
  }

  @Test
  public void shouldReadDescription() {
    //given

    String expectedDescription = "test1";
    //when

    String resultDescription = accountsElements.getDescription();
    //then

    assertThat(resultDescription, is(equalTo(expectedDescription)));
  }

  @Test
  public void shouldReadBalance() {
    //given

    String expectedBalance = "500 $";
    //when

    String resultBalance = accountsElements.getBalance();
    //then

    assertThat(resultBalance, is(equalTo(expectedBalance)));
  }
}