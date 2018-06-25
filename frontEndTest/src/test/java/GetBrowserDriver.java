import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

class GetBrowserDriver {

  private String operatingSystemName = System.getProperty("os.name").toLowerCase();

  WebDriver getWebDriver() {
    if (operatingSystemName.contains("windows")) {
      System.setProperty("webdriver.chrome.driver",
          "src/test/resources/browsers_drivers/windows/chromedriver.exe");
      return new ChromeDriver();
    } else if (operatingSystemName.contains("linux")) {
      System.setProperty("webdriver.chrome.driver",
          "src/test/resources/browsers_drivers/linux/chromedriver");
      return new ChromeDriver();
    } else if (operatingSystemName.contains("mac")) {
      System.setProperty("webdriver.chrome.driver",
          "src/test/resources/browsers_drivers/mac/chromedriver");
      return new ChromeDriver();
    }
    return null;
  }
}