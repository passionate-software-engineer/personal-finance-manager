import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.Test;

public class AccountsScreenTest extends TestBase {

  private static final String URL = "http://localhost:4200/";

  public AccountsScreenTest() {
    super(URL);
  }

  @Test
  public void shouldReadPageTitle() {
    WebElement title = driverWait.until(ExpectedConditions.visibilityOf
        (webDriver.findElement(By.xpath("//div/*[contains(text(),\"Welcome to Personal\")]"))));
    String titleExpected = "Welcome to Personal Finance Manager !";
    String titleResult = title.getText();
    assertThat(titleExpected, is(equalTo(titleResult)));
  }

  @Test
  public void shouldReadId() {
    WebElement elementId = driverWait.until(ExpectedConditions.visibilityOf
        (webDriver.findElement(By.xpath("//app-accounts-list//*[(text()=\"1\")]"))));
    long expectedId = 1L;
    assertThat(Long.valueOf(elementId.getText()), is(equalTo(expectedId)));
  }

  @Test
  public void shouldReadDescription() {
    WebElement elementDescription = driverWait.until(ExpectedConditions.visibilityOf
        (webDriver.findElement(By.xpath("//app-accounts-list//*[contains(text(),\"test\")]"))));
    String expectedDescription = "test1";
    assertThat(elementDescription.getText(), is(equalTo(expectedDescription)));
  }

  @Test
  public void shouldReadBalance() {
    WebElement elementBalance = driverWait.until(ExpectedConditions.visibilityOf
        (webDriver.findElement(By.xpath("//app-accounts-list//*[contains(text(),\"500\")]"))));
    String expectedBalance = "500 $";
    assertThat(expectedBalance, is(equalTo(elementBalance.getText())));
  }
}