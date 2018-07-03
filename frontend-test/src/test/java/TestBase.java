import io.github.bonigarcia.wdm.ChromeDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public abstract class TestBase {

  private String FRONTEND_URL;
  WebDriver webDriver;

  TestBase(String FRONTEND_URL) {
    this.FRONTEND_URL = FRONTEND_URL;
  }

  @BeforeClass
  void setUp() throws IOException {
    new TestHelper().addSampleAccount();
    ChromeDriverManager.getInstance().setup();
    webDriver = new ChromeDriver();
    webDriver.manage().window().maximize();
    webDriver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
    webDriver.get(FRONTEND_URL);
  }

  @AfterClass
  void tearDown() {
    webDriver.quit();
  }
}