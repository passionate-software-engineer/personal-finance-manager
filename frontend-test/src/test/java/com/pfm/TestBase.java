package com.pfm;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public abstract class TestBase {

  WebDriver webDriver;

  @BeforeClass
  void setUp() {
    WebDriverManager.chromedriver().setup();
    ChromeOptions options = new ChromeOptions();
//    options.setHeadless(true);
//    options.addArguments("--disable-gpu");
    webDriver = new ChromeDriver(options);
  }

  @AfterClass
  void tearDown() {
    webDriver.quit();
  }
}