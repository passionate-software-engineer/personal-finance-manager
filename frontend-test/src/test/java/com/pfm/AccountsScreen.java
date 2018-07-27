package com.pfm;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

class AccountsScreen {

  private static final int SECONDS_10 = 10;

  @FindBy(xpath = "//button[@class='btn btn-primary']")
  private WebElement refresh;

  @FindBy(xpath = "//app-accounts-list//td[1]")
  private List<WebElement> id;

  @FindBy(xpath = "//app-accounts-list//td[2]")
  private List<WebElement> description;

  @FindBy(xpath = "//app-accounts-list//td[3]")
  private List<WebElement> balance;

  @FindBy(xpath = "//button[@class='btn btn-danger' and (text()=' Add Account')]")
  private WebElement addAccountButton;

  @FindBy(xpath = "//button[@class='btn btn-danger dropdown-toggle' and (text()='Adding options ')]")
  private WebElement addingOptionsButton;

  @FindBy(xpath = "//*/button[@id='accountScreenOptionsButton' and @class='btn btn-primary dropdown-toggle' and 1]")
  private WebElement editOptions;

  @FindBy(xpath = "//td[4]/button[@class='btn-primary' and 1]")
  private WebElement saveButton;

  @FindBy(xpath = "//div/ul[@class='dropdown-menu']//*[(text()='Exit')]")
  private WebElement exitButton;

  @FindBy(xpath = "//app-accounts-list/table/tbody/tr//td[4]/div/button")
  private List<WebElement> options;

  @FindBy(xpath = "//div[@class='dropdown open']//*[(text()='Delete')]")
  private WebElement delete;

  @FindBy(xpath = "//div[@class='dropdown open']//*[(text()='Edit')]")
  private WebElement edit;

  @FindBy(xpath = "//th[1]/*[@class='glyphicon glyphicon-collapse-up']")
  private WebElement idAscending;

  @FindBy(xpath = "//th[1]/*[@class='glyphicon glyphicon-collapse-down']")
  private WebElement idDescending;

  @FindBy(xpath = "//th[2]/*[@class='glyphicon glyphicon-collapse-up']")
  private WebElement descriptionAscending;

  @FindBy(xpath = "//th[2]/*[@class='glyphicon glyphicon-collapse-down']")
  private WebElement descriptionDescending;

  @FindBy(xpath = "//th[3]/*[@class='glyphicon glyphicon-collapse-up']")
  private WebElement balanceAscending;

  @FindBy(xpath = "//th[3]/*[@class='glyphicon glyphicon-collapse-down']")
  private WebElement balanceDescending;

  @FindBy(xpath = "//app-accounts-list//td[2]/input[1]")
  private WebElement inputDescription;

  @FindBy(xpath = "//app-accounts-list//td[3]/input[1]")
  private WebElement inputBalance;

  AccountsScreen(WebDriver driver) {
    PageFactory.initElements(new AjaxElementLocatorFactory(driver, SECONDS_10), this);
  }

  void refreshButton() {
    refresh.click();
  }

  List<Long> getId() {
    List<Long> idElementsList = new ArrayList<>();
    for (WebElement idElement : id) {
      idElementsList.add(Long.parseLong(idElement.getText()));
    }
    return idElementsList;
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

  void addingOptionsButton() {
    addingOptionsButton.click();
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

  void idAscendingButton() {
    idAscending.click();
  }

  void idDescendingButton() {
    idDescending.click();
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
  }

  void editOptionsButton() {
    editOptions.click();
  }

}