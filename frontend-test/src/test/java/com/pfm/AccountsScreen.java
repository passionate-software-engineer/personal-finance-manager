package com.pfm;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

class AccountsScreen {

  private static final int SECONDS_30 = 30;
  private WebDriver driver;

  @FindBy(xpath = "//button[@class='btn btn-primary']")
  private WebElement refresh;

  @FindBy(xpath = "//app-accounts-list//td[1]")
  private List<WebElement> description;

  @FindBy(xpath = "//app-accounts-list//td[2]")
  private List<WebElement> balance;

  @FindBy(xpath = "//button[@class='btn btn-danger']")
  private WebElement addAccountButton;

  @FindBy(xpath = "//td[3]/button[@class='btn-primary' and 1]")
  private WebElement saveButton;

  @FindBy(xpath = "//app-accounts-list/table/tbody/tr//td[3]/div/button")
  private List<WebElement> options;

  @FindBy(xpath = "//div[@class='dropdown open']//*[(text()='Delete')]")
  private WebElement delete;

  @FindBy(xpath = "//div[@class='dropdown open']//*[(text()='Edit')]")
  private WebElement edit;

  @FindBy(xpath = "//th[1]/*[@class='glyphicon glyphicon-collapse-up']")
  private WebElement descriptionAscending;

  @FindBy(xpath = "//th[1]/*[@class='glyphicon glyphicon-collapse-down']")
  private WebElement descriptionDescending;

  @FindBy(xpath = "//th[2]/*[@class='glyphicon glyphicon-collapse-up']")
  private WebElement balanceAscending;

  @FindBy(xpath = "//th[2]/*[@class='glyphicon glyphicon-collapse-down']")
  private WebElement balanceDescending;

  @FindBy(xpath = "//app-accounts-list//td[1]/input[1]")
  private WebElement inputDescription;

  @FindBy(xpath = "//app-accounts-list//td[2]/input[1]")
  private WebElement inputBalance;

  AccountsScreen(WebDriver driver) {
    this.driver = driver;
    PageFactory.initElements(new AjaxElementLocatorFactory(driver, SECONDS_30), this);
  }

  void refreshButton() {
    refresh.click();
  }

  List<String> getDescription() {
    List<String> descriptionElementsList = new ArrayList<>();
    for (WebElement descriptionElement : description) {
      descriptionElementsList.add(descriptionElement.getText());
    }
    return descriptionElementsList;
  }

  List<BigDecimal> getBalance() {
    List<BigDecimal> balanceElementsList = new ArrayList<>();
    for (WebElement balanceElement : balance) {
      BigDecimal decimalElement = BigDecimal.valueOf(
          Double.parseDouble(balanceElement.getText().replaceAll("[^\\d.]", "")));
      balanceElementsList.add(decimalElement);
    }
    return balanceElementsList;
  }

  void addAccountButton() {
    addAccountButton.click();
  }

  void addDescription(String description) {
    inputDescription.clear();
    inputDescription.sendKeys(description);
  }

  void addBalance(BigDecimal balance) {
    inputBalance.clear();
    inputBalance.sendKeys(String.valueOf(balance));
  }

  void saveOptionButton() {
    saveButton.click();
  }


  void descriptionAscendingButton() {
    descriptionAscending.click();
  }

  void descriptionDescendingButton() {
    descriptionDescending.click();
  }

  void balanceAscendingButton() {
    balanceAscending.click();
  }

  void balanceDescendingButton() {
    balanceDescending.click();
  }

  List<WebElement> optionsButton() {
    return options;
  }

  void editButton() {
    edit.click();
  }

  void deleteButton() {
    delete.click();
    Alert alert = driver.switchTo().alert();
    alert.accept();
  }
}