import io.github.bonigarcia.wdm.ChromeDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

// TODO please start using correct package - simply com.pfm.test
public abstract class TestBase {

  private String frontendUrl;
  WebDriver webDriver; // TODO if it's intended to be used by inheriting classes then use protected, if not private

  // TODO - I don't see sense in passing that from outside - this class should be reponsible for providing that url to all other
  TestBase(String frontendUrl) {
    this.frontendUrl = frontendUrl;
  }

  @BeforeClass
  void setUp() throws IOException {
    TestHelper
        .addSampleAccount(); // TODO It's not correct place - it should be in screen preconditions - this class should stay as generic as possible
    ChromeDriverManager.getInstance().setup();
    webDriver = new ChromeDriver();
    webDriver.manage().window().maximize();
    webDriver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
    webDriver.get(frontendUrl);
  }

  @AfterClass
  void tearDown() {
    webDriver.quit();
  }
}