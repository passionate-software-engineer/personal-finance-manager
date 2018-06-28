import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import io.github.bonigarcia.wdm.ChromeDriverManager;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class FirstScreenTest {

  private WebDriver webDriver;
  private WebDriverWait driverWait;

  @BeforeClass
  private void setUp() {
    ChromeDriverManager.getInstance().setup();
    webDriver = new ChromeDriver();
    driverWait = new WebDriverWait(webDriver, 5);
    webDriver.manage().window().maximize();
    webDriver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
    webDriver.get("http://localhost:4200/");
  }

  @AfterClass
  private void tearDown() {
    webDriver.quit();
  }

  @Test
  public void shouldAddAccount() throws IOException {
    String sampleJson = "{ \"id\":1, \"name\":\"test1\", \"balance\":500 };";
    String response = Objects.requireNonNull(postJson(sampleJson).body()).string();
    String expectedResponse = "1";
    assertThat(response, is(equalTo(expectedResponse)));
  }

  @Test
  public void shouldReadPageTitle() {
    WebElement title = driverWait.until(ExpectedConditions.visibilityOf
        (webDriver.findElement(By.xpath("//div/*[contains(text(),\"Welcome to Personal\")]"))));
    String titleExpected = "Welcome to Personal Finance Manager !";
    String titleResult = title.getText();
    assertThat(titleExpected, is(equalTo(titleResult)));
  }

  @Test(dependsOnMethods = {"shouldAddAccount"})
  public void shouldReadId() {
    WebElement elementId = driverWait.until(ExpectedConditions.visibilityOf
        (webDriver.findElement(By.xpath("//app-accounts-list//*[(text()=\"1\")]"))));
    long expectedId = 1L;
    assertThat(Long.valueOf(elementId.getText()), is(equalTo(expectedId)));
  }

  @Test(dependsOnMethods = {"shouldAddAccount"})
  public void shouldReadDescription() {
    WebElement elementDescription = driverWait.until(ExpectedConditions.visibilityOf
        (webDriver.findElement(By.xpath("//app-accounts-list//*[contains(text(),\"test\")]"))));
    String expectedDescription = "test1";
    assertThat(elementDescription.getText(), is(equalTo(expectedDescription)));
  }

  @Test(dependsOnMethods = {"shouldAddAccount"})
  public void shouldReadBalance() {
    WebElement elementBalance = driverWait.until(ExpectedConditions.visibilityOf
        (webDriver.findElement(By.xpath("//app-accounts-list//*[contains(text(),\"500\")]"))));
    String expectedBalance = "500 $";
    assertThat(expectedBalance, is(equalTo(elementBalance.getText())));
  }

  private Response postJson(String json) throws IOException {
    OkHttpClient client = new OkHttpClient();
    MediaType mediaType = MediaType.parse("application/json");
    RequestBody body = RequestBody
        .create(mediaType, json);
    Request request = new Request.Builder()
        .url("http://localhost:8081/accounts")
        .post(body)
        .build();
    return client.newCall(request).execute();
  }
}