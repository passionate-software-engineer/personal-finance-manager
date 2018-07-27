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
    nameAscending.sort(String::compareToIgnoreCase);
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
    nameDescending.sort(Collections.reverseOrder());
    List<String> resultNameDescending;
    //when
    categoriesScreen.nameDescendingButton();
    resultNameDescending = categoriesScreen.getListOfCategoriesName();

    //then
    assertThat(resultNameDescending, is(equalTo(nameDescending)));
  }

  @Test(dependsOnMethods = {"shouldSortNameDescending"})
  public void shouldSortIdAscending() {
    //given
    List<Long> idAscending = categoriesScreen.getId();
    idAscending.sort(Long::compareTo);
    List<Long> resultIdAscending;

    //when
    categoriesScreen.idAscendingButton();
    resultIdAscending = categoriesScreen.getId();

    //then
    assertThat(resultIdAscending, is(equalTo(idAscending)));
  }

  @Test(dependsOnMethods = {"shouldSortIdAscending"})
  public void shouldSortIdDescending() {
    //given
    List<Long> idDescending = categoriesScreen.getId();
    idDescending.sort(Collections.reverseOrder());
    List<Long> resultIdDescending;

    //when
    categoriesScreen.idDescendingButton();
    resultIdDescending = categoriesScreen.getId();

    //then
    assertThat(resultIdDescending, is(equalTo(idDescending)));
  }

  @Test(dependsOnMethods = {"shouldSortIdDescending"})
  public void shouldDeleteAccount() {
    //given
    List<WebElement> optionsButtonList = categoriesScreen.optionsButton();
    List<String> resultListOfCategories;
    List<String> deletedCategories = new ArrayList<>();

    //when
    for (int i = 0; i < 2; i++) {
      deletedCategories.add(categoriesScreen.getListOfCategoriesName().get(i));
      optionsButtonList.get(i).click();
      categoriesScreen.deleteButton();
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
      Thread.sleep(500);
      categoriesScreen.editButton();
      categoriesScreen.setCategoryName(categoryNameList[i]);
      categoriesScreen.save();
    }

    webDriver.navigate().refresh();
    resultCategoryNameList = categoriesScreen.getListOfCategoriesName();

    //then
    for (String aCategoryNameList : categoryNameList) {
      assertThat(resultCategoryNameList.contains(aCategoryNameList), is(true));
    }
  }
}
