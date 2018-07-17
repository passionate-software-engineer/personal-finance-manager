package com.pfm;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.pfm.helpers.TestHelper;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class AccountsScreenTest extends TestBase {

  // TODO take this value from System properties / gradle properties - it should be possible to provide this value from outside
  private static final String FRONTEND_URL = "http://localhost:4200/";

  private AccountsScreen accountsScreen;

  @BeforeClass
  void getElements() {
    webDriver.get(FRONTEND_URL);
    accountsScreen = new AccountsScreen(webDriver);
  }

  @Test
  public void shouldAddAccount() {
    //given
    Random random = new Random();
    long randomNumber = random.nextInt(100000);
    String[] expectedListOfDescription =
        {"bzwbk number: " + randomNumber, "mbank number: " + randomNumber,
            "alior number: " + randomNumber, "pko number: " + randomNumber,
            "ing number: " + randomNumber};
    BigDecimal[] expectedListOfBalance =
        {BigDecimal.valueOf(100.25).add(BigDecimal.valueOf(randomNumber)),
            BigDecimal.valueOf(500.48).add(BigDecimal.valueOf(randomNumber)),
            BigDecimal.valueOf(50.00).add(BigDecimal.valueOf(randomNumber)),
            BigDecimal.valueOf(666.78).add(BigDecimal.valueOf(randomNumber)),
            BigDecimal.valueOf(2.82).add(BigDecimal.valueOf(randomNumber))};
    List<String> resultListOfDescription;
    List<BigDecimal> resultListOfBalance;

    //when
    for (int i = 0; i < expectedListOfDescription.length; i++) {
      accountsScreen.addAccountButton();
      accountsScreen.addDescription(expectedListOfDescription[i]);
      accountsScreen.addBalance(expectedListOfBalance[i]);
      accountsScreen.addingOptionsButton();
      accountsScreen.saveOptionButton();
    }
    resultListOfDescription = accountsScreen.getDescription();
    resultListOfBalance = accountsScreen.getBalance();

    //then
    for (int i = 0; i < expectedListOfBalance.length; i++) {
      assertThat(resultListOfDescription.contains(expectedListOfDescription[i]),
          is(true));
      assertThat(resultListOfBalance.contains(expectedListOfBalance[i]), is(true));
    }
  }

  @Test
  public void shouldSortDescriptionAscending() {
    //given
    List<String> descriptionAscending = accountsScreen.getDescription();
    descriptionAscending.sort(String::compareToIgnoreCase);
    List<String> resultDescriptionAscending;

    //when
    accountsScreen.descriptionAscendingButton();
    resultDescriptionAscending = accountsScreen.getDescription();

    //then
    assertThat(resultDescriptionAscending, is(equalTo(descriptionAscending)));
  }

  @Test(dependsOnMethods = {"shouldSortDescriptionAscending"})
  public void shouldSortDescriptionDescending() {
    //given
    List<String> descriptionDescending = accountsScreen.getDescription();
    descriptionDescending.sort(Collections.reverseOrder());
    List<String> resultDescriptionDescending;

    //when
    accountsScreen.descriptionDescendingButton();
    resultDescriptionDescending = accountsScreen.getDescription();

    //then
    assertThat(resultDescriptionDescending, is(equalTo(descriptionDescending)));
  }

  @Test(dependsOnMethods = {"shouldSortDescriptionDescending"})
  public void shouldSortIdAscending() {
    //given
    List<Long> idAscending = accountsScreen.getId();
    idAscending.sort(Long::compareTo);
    List<Long> resultIdAscending;

    //when
    accountsScreen.idAscendingButton();
    resultIdAscending = accountsScreen.getId();

    //then
    assertThat(resultIdAscending, is(equalTo(idAscending)));
  }

  @Test(dependsOnMethods = {"shouldSortIdAscending"})
  public void shouldSortIdDescending() {
    //given
    List<Long> idDescending = accountsScreen.getId();
    idDescending.sort(Collections.reverseOrder());
    List<Long> resultIdDescending;

    //when
    accountsScreen.idDescendingButton();
    resultIdDescending = accountsScreen.getId();

    //then
    assertThat(resultIdDescending, is(equalTo(idDescending)));
  }

  @Test(dependsOnMethods = {"shouldSortIdDescending"})
  public void shouldSortBalanceAscending() {
    //given
    List<BigDecimal> balanceAscending = accountsScreen.getBalance();
    balanceAscending.sort(BigDecimal::compareTo);
    List<BigDecimal> resultBalanceAscending;

    //when
    accountsScreen.balanceAscendingButton();
    resultBalanceAscending = accountsScreen.getBalance();

    //then
    assertThat(resultBalanceAscending, is(equalTo(balanceAscending)));
  }

  @Test(dependsOnMethods = {"shouldSortBalanceAscending"})
  public void shouldSortBalanceDescending() {
    //given
    List<BigDecimal> balanceDescending = accountsScreen.getBalance();
    balanceDescending.sort(Collections.reverseOrder());
    List<BigDecimal> resultBalanceDescending;

    //when
    accountsScreen.balanceDescendingButton();
    resultBalanceDescending = accountsScreen.getBalance();

    //then
    assertThat(resultBalanceDescending, is(equalTo(balanceDescending)));
  }

  @Test(dependsOnMethods = {"shouldSortBalanceDescending"})
  public void shouldDeleteAccount() {
    //given
    List<WebElement> optionsButtonList = accountsScreen.optionsButton();
    List<String> resultListOfDescription;
    List<BigDecimal> resultListOfBalance;
    List<String> deletedDescription = new ArrayList<>();
    List<BigDecimal> deletedBalance = new ArrayList<>();

    //when
    for (int i = 0; i < 2; i++) {
      deletedDescription.add(accountsScreen.getDescription().get(i));
      deletedBalance.add(accountsScreen.getBalance().get(i));
      optionsButtonList.get(i).click();
      accountsScreen.deleteButton();
    }
    resultListOfDescription = accountsScreen.getDescription();
    resultListOfBalance = accountsScreen.getBalance();

    //then
    for (int i = 0; i < deletedBalance.size(); i++) {
      assertThat(resultListOfDescription.contains(deletedDescription.get(i)), is(false));
      assertThat(resultListOfBalance.contains(deletedBalance.get(i)), is(false));
    }
  }

  @Test(dependsOnMethods = {"shouldDeleteAccount"})
  public void shouldUpdateAccount() {
    //given
    List<WebElement> optionsButtonList = accountsScreen.optionsButton();
    String[] descriptionsList = {"pekao", "mienium", "santander"};
    BigDecimal[] balanceList =
        {BigDecimal.valueOf(77.77), BigDecimal.valueOf(12.12), BigDecimal.valueOf(0.45)};
    List<String> resultListOfDescription;
    List<BigDecimal> resultListOfBalance;

    //when
    for (int i = 0; i < 3; i++) {
      optionsButtonList.get(i).click();
      accountsScreen.editButton();
      accountsScreen.addDescription(descriptionsList[i]);
      accountsScreen.addBalance(balanceList[i]);
      accountsScreen.editOptionsButton();
      accountsScreen.saveOptionButton();
    }

    webDriver.navigate().refresh();
    resultListOfDescription = accountsScreen.getDescription();
    resultListOfBalance = accountsScreen.getBalance();

    //then
    for (int i = 0; i < descriptionsList.length; i++) {
      assertThat(resultListOfDescription.contains(descriptionsList[i]), is(true));
      assertThat(resultListOfBalance.contains(balanceList[i]), is(true));
    }
  }

  @Test(dependsOnMethods = {"shouldUpdateAccount"})
  public void shouldRefreshPage() throws IOException {
    //given
    BigDecimal sampleBalance = BigDecimal.valueOf(320.00);
    String sampleDescription = "ideaBank";
    List<String> resultListOfDescription;
    List<BigDecimal> resultListOfBalance;

    //when
    TestHelper.addSampleAccount();
    resultListOfDescription = accountsScreen.getDescription();
    resultListOfBalance = accountsScreen.getBalance();
    assertThat(resultListOfBalance.contains(sampleBalance), is(false));
    assertThat(resultListOfDescription.contains(sampleDescription), is(false));

    accountsScreen.refreshButton();

    resultListOfDescription = accountsScreen.getDescription();
    resultListOfBalance = accountsScreen.getBalance();

    //then
    assertThat(resultListOfBalance.contains(sampleBalance), is(true));
    assertThat(resultListOfDescription.contains(sampleDescription), is(true));
  }

  @AfterClass
  void tearDown() {
    List<WebElement> optionsButtonList = accountsScreen.optionsButton();
    optionsButtonList.get(optionsButtonList.size()-1).click();
    accountsScreen.deleteButton();

  }
}