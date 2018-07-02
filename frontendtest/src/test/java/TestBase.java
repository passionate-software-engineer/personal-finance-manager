import io.github.bonigarcia.wdm.ChromeDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public abstract class TestBase {

  private String URL;
  private static final int SECONDS_5 = 5;
  WebDriver webDriver;
  WebDriverWait driverWait;

  TestBase(String URL) {
    this.URL = URL;
  }

  @BeforeClass
  void setUp() throws IOException, InterruptedException {
    new TestHelper().addSampleAccount();
    ChromeDriverManager.getInstance().setup();
    webDriver = new ChromeDriver();
    driverWait = new WebDriverWait(webDriver, SECONDS_5);
    webDriver.manage().window().maximize();
    webDriver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
    webDriver.get(URL);

    //TODO fix the problem with selenium.NoSuchElementException, the problems occur even with Implicit & Explicit Wait
    // temporary solution
    Thread.sleep(500);
  }

  @AfterClass
  void tearDown() {
    webDriver.quit();
  }
}