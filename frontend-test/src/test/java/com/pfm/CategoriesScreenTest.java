package com.pfm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

import org.hamcrest.CoreMatchers;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CategoriesScreenTest extends TestBase {

  private static final String FRONTEND_URL
      = "http://personal-finance-manager.s3-website.us-east-2.amazonaws.com/categories";

  private CategoriesScreen categoriesScreen;

  @BeforeClass
  void getElements() {
    webDriver.get(FRONTEND_URL);
    categoriesScreen = new CategoriesScreen(webDriver);
  }

  @Test
  public void shouldRemoveAllAccountsBeforeTest() throws InterruptedException {
    List<WebElement> optionsButtonList = categoriesScreen.optionsButton();
    //when
    for (WebElement anOptionsButtonList : optionsButtonList) {
      anOptionsButtonList.click();
      categoriesScreen.deleteButton();
      Thread.sleep(500);
    }
    optionsButtonList = categoriesScreen.optionsButton();
    assertThat(optionsButtonList.size(), CoreMatchers.is(0));
  }

  @Test(dependsOnMethods = {"shouldRemoveAllAccountsBeforeTest"})
  public void shouldAddCategory() throws InterruptedException {
    //given
    String[] expectedListOfCategoriesName = {"Car", "Home", "Sport", "Entertainment", "Food"};
    List<String> resulListOfCategoriesName;

    //when
    for (String anExpectedListOfCategoriesName : expectedListOfCategoriesName) {
      categoriesScreen.addCategory();
      categoriesScreen.setCategoryName(anExpectedListOfCategoriesName);
      categoriesScreen.selectCategory();
      categoriesScreen.setSelectMainCategory();
      categoriesScreen.save();
      Thread.sleep(500);
    }
    resulListOfCategoriesName = categoriesScreen.getListOfCategoriesName();

    //then
    for (String anExpectedListOfCategoriesName : expectedListOfCategoriesName) {
      assertThat(resulListOfCategoriesName.contains(anExpectedListOfCategoriesName), is(true));
    }
  }

  @Test(dependsOnMethods = {"shouldAddCategory"})
  public void shouldSortNameAscending() {
    //given
    List<String> nameAscending = categoriesScreen.getListOfCategoriesName();
    nameAscending.sort(Collections.reverseOrder());
    List<String> resultNameAscending;

    //when
    categoriesScreen.nameAscendingButton();
    resultNameAscending = categoriesScreen.getListOfCategoriesName();

    //then
    assertThat(resultNameAscending, is(equalTo(nameAscending)));
  }

  @Test(dependsOnMethods = {"shouldSortNameAscending"})
  public void shouldSortNameDescending() {
    //given
    List<String> nameDescending = categoriesScreen.getListOfCategoriesName();
    nameDescending.sort(String::compareToIgnoreCase);
    List<String> resultNameDescending;
    //when
    categoriesScreen.nameDescendingButton();
    resultNameDescending = categoriesScreen.getListOfCategoriesName();

    //then
    assertThat(resultNameDescending, is(equalTo(nameDescending)));
  }

  @Test(dependsOnMethods = {"shouldSortNameDescending"})
  public void shouldDeleteAccount() throws InterruptedException {
    //given
    List<WebElement> optionsButtonList = categoriesScreen.optionsButton();
    List<String> resultListOfCategories;
    List<String> deletedCategories = new ArrayList<>();

    //when
    for (int i = 0; i < 2; i++) {
      deletedCategories.add(categoriesScreen.getListOfCategoriesName().get(i));
      optionsButtonList.get(i).click();
      categoriesScreen.deleteButton();
      Thread.sleep(500);
    }
    resultListOfCategories = categoriesScreen.getListOfCategoriesName();

    //then
    for (String deletedCategory : deletedCategories) {
      assertThat(resultListOfCategories.contains(deletedCategory), is(false));
    }
  }

  @Test(dependsOnMethods = {"shouldDeleteAccount"})
  public void shouldUpdateAccount() throws InterruptedException {
    //given
    List<WebElement> optionsButtonList = categoriesScreen.optionsButton();
    String[] categoryNameList = {"Holiday", "Books", "Clothes"};
    List<String> resultCategoryNameList;

    //when
    for (int i = 0; i < 3; i++) {
      optionsButtonList.get(i).click();
      categoriesScreen.editButton();
      Thread.sleep(500);
      categoriesScreen.setCategoryName(categoryNameList[i]);
      categoriesScreen.save();
      Thread.sleep(500);
    }

    webDriver.navigate().refresh();
    resultCategoryNameList = categoriesScreen.getListOfCategoriesName();

    //then
    for (String aCategoryNameList : categoryNameList) {
      assertThat(resultCategoryNameList.contains(aCategoryNameList), is(true));
    }
  }
}