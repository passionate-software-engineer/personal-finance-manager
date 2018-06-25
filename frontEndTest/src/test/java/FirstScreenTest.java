import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class FirstScreenTest {

  private WebDriver webDriver;

  @BeforeClass
  private void setUp() {
    webDriver = new GetBrowserDriver().getWebDriver();
    webDriver.manage().window().maximize();
    webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    webDriver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
    webDriver.get("http://localhost:4200/");
  }

  @AfterClass
  private void tearDown() {
    webDriver.quit();
  }

  @Test
  public void ShouldReadPageTitle() {
    WebElement title = webDriver.findElement(By.xpath("/html/body/app-root/div/h1"));
    String titleExpected = "Welcome to Personal Finance Manager !";
    String titleResult = title.getText();
    assertThat(titleExpected, is(equalTo(titleResult)));
  }

  @Test
  public void shouldAddAccount() throws IOException, InterruptedException {
    String sampleJson = "{ \"id\":1, \"name\":\"test1\", \"balance\":500 };";
    String response = Objects.requireNonNull(postJson(sampleJson).body()).string();
    String expectedResponse = "1";
    assertThat(response, is(equalTo(expectedResponse)));
    Thread.sleep(1000);
  }

  @Test(dependsOnMethods = {"shouldAddAccount"})
  public void shouldReadId() {
    WebElement elementId = webDriver
        .findElement(By.xpath("/html/body/app-root/div/app-accounts-list/table/tbody/tr/td[1]"));
    long expectedId = 1L;
    assertThat(Long.valueOf(elementId.getText()), is(equalTo(expectedId)));
  }

  @Test(dependsOnMethods = {"shouldAddAccount"})
  public void ShouldReadDescription() {
    WebElement elementId = webDriver
        .findElement(By.xpath("/html/body/app-root/div/app-accounts-list/table/tbody/tr[1]/td[2]"));
    String expectedDescription = "test1";
    assertThat(elementId.getText(), is(equalTo(expectedDescription)));
  }

  @Test(dependsOnMethods = {"shouldAddAccount"})
  public void ShouldReadBalance() {
    WebElement elementId = webDriver
        .findElement(By.xpath("/html/body/app-root/div/app-accounts-list/table/tbody/tr[1]/td[3]"));
    String expectedBalance = "500 $";
    assertThat(expectedBalance, is(equalTo(elementId.getText())));
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