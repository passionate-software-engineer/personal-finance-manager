import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

class AccountsScreenElements {

  private static final int SECONDS_10 = 10;

  // TODO - locating by text is not good idea - you should locate by id (request Seba to provide it) and then check if value is as expected
  @FindBy(xpath = "//div/*[contains(text(),\"Welcome to Personal\")]")
  private WebElement title;

  @FindBy(xpath = "//app-accounts-list//*[(text()=\"1\")]")
  private WebElement id;

  @FindBy(xpath = "//app-accounts-list//*[contains(text(),\"mbank\")]")
  private WebElement description;

  @FindBy(xpath = "//app-accounts-list//*[contains(text(),\"500\")]")
  private WebElement balance;

  AccountsScreenElements(WebDriver driver) {
    PageFactory.initElements(new AjaxElementLocatorFactory(driver, SECONDS_10), this);
  }

  String getTitle() {
    return title.getText();
  }

  long getId() {
    return Long.valueOf(id.getText());
  }

  String getDescription() {
    return description.getText();
  }

  String getBalance() {
    return balance.getText();
  }
}