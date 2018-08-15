package com.pfm;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.util.ArrayList;
import java.util.List;

class CategoriesScreen {

  private static final int SECONDS_30 = 30;

  private WebDriver driver;

  @FindBy(xpath = "//*[@class='btn btn-danger']")
  private WebElement addCategoryBt;

  @FindBy(xpath = "//app-categories/table/tbody/tr//td[3]/div/button")
  private List<WebElement> options;

  @FindBy(xpath = "//div[@class='dropdown open']//*[(text()='Delete')]")
  private WebElement delete;

  @FindBy(xpath = "//app-categories/table/tbody/tr/td[1]/input")
  private WebElement inputCategoryName;

  @FindBy(xpath = "//select[@id='selectedParentCategory']")
  private WebElement selectCategoryBt;

  @FindBy(xpath = "//td/button[@class='btn-primary' and 1]")
  private WebElement saveBt;

  @FindBy(xpath = "//div[@class='dropdown open']//*[(text()='Edit')]")
  private WebElement edit;

  @FindBy(xpath = "//*[@id='selectedParentCategory']/option[1]")
  private WebElement selectMainCategoryBt;

  @FindBy(xpath = "//app-categories//td[1]")
  private List<WebElement> listOfCategoriesName;

  @FindBy(xpath = "//th[1]/*[@class='glyphicon glyphicon-collapse-up']")
  private WebElement nameAscending;

  @FindBy(xpath = "//th[1]/*[@class='glyphicon glyphicon-collapse-down']")
  private WebElement nameDescending;

  CategoriesScreen(WebDriver driver) {
    this.driver = driver;
    PageFactory.initElements(new AjaxElementLocatorFactory(driver, SECONDS_30), this);
  }

  void addCategory() {
    addCategoryBt.click();
  }

  void setCategoryName(String categoryName) {
    inputCategoryName.clear();
    inputCategoryName.sendKeys(categoryName);
  }

  void selectCategory() {
    selectCategoryBt.click();
  }

  void save() {
    saveBt.click();
  }

  void setSelectMainCategory() {
    selectMainCategoryBt.click();
  }

  List<String> getListOfCategoriesName() {
    List<String> categoriesNamesList = new ArrayList<>();
    for (WebElement categoryElement : listOfCategoriesName) {
      categoriesNamesList.add(categoryElement.getText());
    }
    return categoriesNamesList;
  }

  void nameAscendingButton() {
    nameAscending.click();
  }

  void nameDescendingButton() {
    nameDescending.click();
  }

  List<WebElement> optionsButton() {
    return options;
  }

  void deleteButton() {
    delete.click();
    Alert alert = driver.switchTo().alert();
    alert.accept();
  }

  void editButton() {
    edit.click();
  }
}